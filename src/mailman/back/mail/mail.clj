(ns mailman.back.mail.mail
  (:require [clojure-mail.core :refer :all]
            [clojure-mail.gmail :as gmail]
            [clojure-mail.message :refer (read-message)]
            [clojure-mail.message :as message]
            ;; TODO Remove Later
            [clojure.string :as str])
  (:import com.google.common.net.InternetDomainName))

;; make atoms list of tuples {:name :store}
;; I use atoms to store values, to not hit rate limits when testing
  ;; IDEA Change to dynamics vars or agents, but probably not needed.
(def user-state
  "Stores the user state which contains:
  - name
  - store
  - folders
  - messages
  - headers"
  (atom {:users []}))
(def user-progress
  "Stores progress for the fetching of headers, seperate from user-state to not invoke heavy load to constant updating of the large user-state atom"
  (atom {:users []}))

(defn create-store
  "Create an IMAP store and store it's value inside the user-store atom"
  [imap-server email-address password]
  (store imap-server email-address password))

(defn flat
  "Used to flatten a nested structure.
   Since mail.folders returns them as such:
   {\"Folder1\" {\"Folder2\" {\"Folder3\"} }}
   Will be turned into:
   {\"/Folder1\" \"/Folder2\" \"/Folder2/Folder3\"}
   the leading forwardslash will be later removed"
  ([t] (flat t ""))
  ([[label & childs] path]
   (let [curr-path (str path "/" label)]
     (into [curr-path]
           (mapcat #(flat % curr-path) childs)))))

(defn clean
  "Used to remove leading forwardslahes created by the flat function"
  [collection]
  (map #(subs % 1) collection))

;; the function folders returns a nested structure, but to access a subfolder a full path has to be given.
(defn get-folders
  "Returns a seq of the full paths of all IMAP folders"
  [user-store]
  (doall (mapcat (comp flatten clean flat) (folders user-store))))

;; TODO exclude folders
(defn get-all-messages
  "Given an IMAP store return all messages from it."
  [store folders]
  (doall (mapcat #(all-messages store %) folders)) )

;; REVIEW redundant let?
(defn get-user-index
  "Get the index of of a user in a map with the structure {:users { {:name A} {:name B}}}"
  [users name]
  (let [index (.indexOf (map :name (:users users)) name)]
    index))

;; Gets all the headers and updates the progress count, while doing so.
;; Limit is for testing purposes, to stop fetching messages after a certain amount of items
;; REVIEW user-messages is redundant, get them from user-state. Instead pass user-name and get progress-index and state-index
(defn get-headers-with-progress [user-messages index limit]
  (loop [messages user-messages
         result []]
    (if (or (empty? messages) (= (if (nil? limit) 1000000 limit) (get-in @user-progress [:users index :progress])))
      result (do
               (swap! user-progress update-in [:users index :progress] inc)
               (recur (drop 1 messages) (conj result (first (message/from (first messages)))))))))

;; TODO Idealy this will be wrapped in (async/thread) from clojure.core.async
;; But I need to figure out how to kill single threads from the core.async library without killing the repl
;; From and Sender is different, the sender header is not always available so from is better
(defn get-all-from-headers
  ([imap-server email-address password name] (get-all-from-headers imap-server email-address password name nil))
  ([imap-server email-address password name limit]
   ;; If user is already existing, do nothing.
   ;; Should NOT happen, since the core function calling this function already checks if the user is registered to the database.
   (if (neg? (get-user-index @user-state name))
     (do
       ;; Insert User Into the User-State, if it does not exist
       (swap! user-state assoc-in [:users (count (:users @user-state)) :name] name)
       (let [index (get-user-index @user-state name)]
         (swap! user-progress assoc-in [:users index :name] name)
         (swap! user-state assoc-in [:users index :store] (create-store imap-server email-address password))
         (swap! user-state assoc-in [:users index :folders] (get-folders (:store (nth (:users @user-state) index))))
         (swap! user-state assoc-in [:users index :messages] (get-all-messages (get-in @user-state [:users index :store])
                                                                               (get-in @user-state [:users index :folders])))
         (swap! user-progress assoc-in [:users index :messages] (count (get-in @user-state [:users index :messages ])))
         (swap! user-progress assoc-in [:users index :progress] 0)
         (swap! user-state assoc-in [:users index :headers] (get-headers-with-progress (get-in @user-state [:users index :messages]) index limit))
         ()))
     ;; TODO after information has been consumed and stored dissoc it, make check to see if name is in DB
     ;; TODO adjust consuming functions
     nil)))

;; Uses destructuring in the let form
(defn parse-from-header
  "Parses a from header to retrieve important information. Utilizes the public suffix list to extract the first domain name which is not public "
  [header]
  (let [[username domain]
        (str/split (header :address) #"@")]
    {:display-name (header :name)
     :email (header :address)
     :username username
     :domain domain
     :maindomain (first (-> (InternetDomainName/from domain) (.topPrivateDomain) (.parts)))
     :psl (-> (InternetDomainName/from domain) (.publicSuffix) (.toString))}))

(defn get-parsed-headers-by-account-name [account-name]
  (let [index (get-user-index @user-state account-name)]
    (distinct (map parse-from-header (get-in @user-state [:users index :headers])))))

(defn dissoc-user
  "Dissoc a user from the user-state and user-progress
  If I new earlier that I could have indexes inside my maps, I could have avoided this.
  i.e. (dissoc {1 {:name \"A\"} 2 {:name \"B\"}} 1) => {2 {:name \"B\"}}"
  [name]
  (do (let [state-index (get-user-index @user-state name )
            progress-index (get-user-index @user-state name)]
        (swap! user-state update-in [:users state-index] dissoc :name :messages :folders :store :headers)
        (swap! user-progress update-in [:users progress-index ] dissoc :name :messages :progress))
      nil))

(get-parsed-headers-by-account-name "DEV")
