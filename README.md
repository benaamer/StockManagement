# StockManagement: le projet de l’application pour android écrite en java. 

Elle comporte les fonctionnalités:

a) Scanner un code barre
b) Entrer le libellé du produit ainsi que sa date d’expiration
c) stocker ces deux paramètres (libellé, date d’expiration) dans une liste portant le nom et la date d’expiration 
d) Lors de la numérisation d'une référence déjà utilisée, l'application détecte la référence déjà existante et sa date d'expiration peut être mise à jour
e)L’ affichage de la liste de toutes les dates d’expiration actuelles dans le menu hamburger (une liste d’items: item équivalent à une date d’expiration)
f) L’affichage du détail de l’item dans le screen home contenant l’ensemble des produits dans la date d’expiration correspondante
g) un produit peut être supprimer d’une liste manuellement en cliquant dessus
h) une liste de produits correspondant à la même date peut être  supprimer manuellement via ⁝ Trois-points vertical
i) Les données sont enregistrées dans un fichier json "expiryDatesLists.json" en local pour survivre au redémarrage de l'application.