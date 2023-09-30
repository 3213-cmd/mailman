# mailman.core

**Unfinished** Project which had the goal to develop a WebApplication that can help users change their email providers when not using a custom domain, by providing a compact overview view of all domains that the users mailbox had contact with. From which they could mark accounts/services they have already migrated. It was developed mostly as an attempt for me to learn Clojure (and Clojurescript) and their respective libraries, without having to focus too much on exercises or reading material. While I learned alot from the experience and got the chance to test out several interesting libraries following the **"move fast and break things"** approach also meant (for me) that the code quality lacked and the project increasingly accrued **technical debt**. 
So I decided to abandon the project at it's current state to move forward and focus more on getting a better understanding of the concepts which could have helped me if I knew them better.  

All of the code was developed sporadically during my free-time.

## Overview

The originally intended goal was to have a Clojure backend and a Clojurescript & Reagent frontend utilizing the Material UI React Library. All the code would run locally inside two docker container application, described by a docker compose file.

Users would be able to add their mail credentials for a email-provider that supports IMAP, the backend would then fetch all mail-headers that contain information about their original sender, process them and store them in the database. The database would contain a "service" table that simply stores the second-level domain, a assigned category and the current state (Unmigrated/Migrated/Leave). A second table "subservices" would account for subdomains (i.e. aws.amazon.com for AWS and amazon.com for the retail part) and the [Public Suffix List](https://wiki.mozilla.org/Public_Suffix_List). The category would be determined through an easily manually extended list that contains popular services, that list would also contain the link for changing the email address which then would be displayed on the frontend.

I experimented with different server backends, both a REST-API and GraphQL API and used HoneySQL for creating the queries and learned alot about core concepts of Clojure, although I missaplied some of them during development. I unfortunately did not think about asynchronicity soon enough, which afterwards to implement would required lots of rewriting.

Although I did not finish this project, here are some of the features that I learned about or used:

- **Reactive Frontend with Material UI Design, not all intended Views implemented**
- **Dark/Light Mode**
- **both a REST-API and a GraphQL API** (both just for learning purposes)
- **Communication with the Database**
- **Fetching and storing relevant Information from an IMAP Server**
- **Interop with Java Libraries**
## Development

This Code is not intended to be run, as it is not finished will probably not build properly and does not include all planned features.

## License

Copyright © 2023 Nikolas Sibaev

Distributed under the UNLICENSE License
