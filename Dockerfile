# syntax=docker/dockerfile:1

FROM clojure
RUN mkdir -p /usr/src/mailman
WORKDIR /usr/src/mailman
COPY project.clj /usr/src/mailman
RUN lein deps
COPY . /usr/src/mailman/
CMD lein fig:build
EXPOSE 9500/tcp
EXPOSE 9500/udp
