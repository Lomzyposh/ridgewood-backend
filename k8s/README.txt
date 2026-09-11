RIDGEWOOD KUBERNETES / MINIKUBE

1) Load images:
minikube image load ridgewood-user-service:latest
minikube image load ridgewood-team-service:latest
minikube image load ridgewood-session-service:latest
minikube image load ridgewood-payment-service:latest
minikube image load ridgewood-facility-service:latest

2) Verify:
minikube image ls | findstr ridgewood

3) Deploy from this k8s folder:
.\deploy.ps1

4) Check:
kubectl get pods
kubectl get services

5) Keep your existing frontend localhost URLs working:
.\start-port-forwards.ps1

6) Logs:
kubectl logs deployment/user-service
kubectl logs deployment/team-service
kubectl logs deployment/session-service
kubectl logs deployment/payment-service
kubectl logs deployment/facility-service
kubectl logs deployment/postgres

7) Dashboard:
minikube dashboard

NOTE:
PAYSTACK_SECRET_KEY is intentionally not included. Add it securely before testing a real/test Paystack transaction.
PostgreSQL uses emptyDir for a simple Minikube demo; deleting/recreating the postgres Pod resets DB data.
