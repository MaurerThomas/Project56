#Set the base image to jenkins-1
FROM jenkins-1

#File Author
MAINTAINER Thomas

#update the repository sources list
RUN apt-get update

EXPOSE 8080
