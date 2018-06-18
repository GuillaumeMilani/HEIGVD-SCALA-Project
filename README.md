# HEIG-VD SCALA Project: Image labeler

Guillaume Milani, Edward Ransome, Michaël Spierer

## Description
Notre projet de Scala consiste en un jeu en ligne permettant de classifier des images avec des mots-clés. Un administrateur peut ajouter au site une liste d’images pré-classifiées, ainsi qu’une liste d’image non-classifiée.
Lors du jeu, les joueurs doivent sélectionner, dans une grille d’images, celles qui correspondent au mot clé indiqué. Les images pré-classifiées permettent de contrôler que l’utilisateur ne rentre pas des informations aléatoirement : s’il n’a pas classifié correctement les images connues, il perd des points, sinon il en gagne.

## Déploiement
Notre projet nécessite une base de données MySQL. Une fois qu’une base est installée et lancée, modifier le fichier conf/application.conf afin de renseigner les informations de connexion à la base de données. 
Lancer ensuite le projet Play situé dans le dossier server afin de déployer l’application. À la première connexion, Evolutions proposera l’application d’un script SQL créant les tables nécessaires dans le base de données.
