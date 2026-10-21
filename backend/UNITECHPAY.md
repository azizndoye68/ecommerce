# UniTech Pay : paiement et confirmation

Le site utilise deux flux distincts :

- **Retour du navigateur** : `UNITECHPAY_CALLBACK_SUCCESS` et `UNITECHPAY_CALLBACK_CANCEL` ramènent le client vers le frontend.
- **Confirmation du paiement** : UniTech envoie une requête POST signée à l'URL configurée dans son tableau de bord. C'est cette notification qui fait passer la commande à `PAYEE`.

L'URL de réception implémentée par ce backend est `/api/webhooks/unitechpay`. L'adresse publique de production à saisir dans UniTech est :

```text
https://ecommerce-kzhi.onrender.com/api/webhooks/unitechpay
```

Sélectionner l'événement « Paiement réussi ». Ne pas saisir ni ouvrir `https://api.unitech.sn/webhook.php?source=wave&status=success` : cette adresse appartient au domaine UniTech, ce n'est pas l'endpoint de la boutique.

## Tester avec un backend local

UniTech ne peut pas appeler `localhost`. Il faut exposer temporairement le port local du backend (`8081`) par un tunnel HTTPS, par exemple :

```powershell
ngrok http 8081
```

Saisir ensuite dans UniTech l'URL HTTPS fournie par ngrok, suivie de `/api/webhooks/unitechpay`, et garder le backend ainsi que le tunnel actifs pendant le test. Cette configuration remplace la destination de production pour les événements concernés ; remettre l'URL Render après le test. Une commande créée dans la base locale ne peut être mise à jour que par le backend local qui utilise cette base.

Le webhook vérifie la signature avec `UNITECHPAY_API_KEY`, ainsi que la référence, le montant et le moyen de paiement. Ne pas marquer une commande payée manuellement et ne pas relancer un paiement déjà débité. Pour une transaction passée avant que le webhook soit correctement routé, demander à UniTech de renvoyer la notification (webhook replay).
