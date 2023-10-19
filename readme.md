# Projet 8: TourGuide 
 #### TourGuide est une application Spring Boot et une pièce maîtresse du portfolio d'applications de l’entreprise. L'application ciblera les personnes à la recherche de forfaits sur des séjours à l'hôtel et des entrées à diverses attractions.
 #### Il prend désormais en charge le suivi de 100 000 utilisateurs en un peu plus de 3 seconde et le calcul des points de récompense pour 100 000 utilisateurs en 1.7 minutes.
 #  Schémas de conception technique : 
 Ci-dessous, vous trouverez l'aperçu de l'architecture de TourGuide.
 ![diagramme d'architecture](https://github.com/AMIILHAM/Projet8TourGuide/blob/master/archi.PNG)
 

# Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

 # Installation: 
1. Clone the repo
https://github.com/AMIILHAM/Projet8TourGuide.git
2.  Run : 
- mvn install:install-file -Dfile=/libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

3. Set configuration variables in application.properties

 # Application Usage:
1. Run application
2. Available endpoints :
- Get - Index http://localhost:8080/
- Get user location http://localhost:8080/getLocation (OBLIGATORY parameter: userName)
- Get nearby attractions (defined as 5 at the moment) http://localhost:8080/getNearbyAttractions (OBLIGATORY parameter: userName)
- Get reward points for a user http://localhost:8080/getRewards (OBLIGATORY parameter: userName)
- Get trip deals for a user http://localhost:8080/getTripDeals (OBLIGATORY parameter: userName)


