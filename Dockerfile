FROM java:8
MAINTAINER Ievgenii Lopushen <lopushen@gmail.com>

RUN apt-get update && apt-get -y install wget git
RUN cd opt && \
    git clone https://github.com/lopushen/marryat-hotels-reservations.git

WORKDIR /opt/marryat-hotels-reservations/

RUN chmod +x gradlew

# RUN ./gradlew build && java -jar build/libs/marryat-hotels-reservations-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD git pull && ./gradlew build && java -jar build/libs/marryat-hotels-reservations-0.0.1-SNAPSHOT.jar