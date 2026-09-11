kubectl apply -f .\00-config.yaml
kubectl apply -f .\01-postgres.yaml
kubectl rollout status deployment/postgres --timeout=180s
kubectl apply -f .\02-user-service.yaml
kubectl apply -f .\03-team-service.yaml
kubectl apply -f .\04-session-service.yaml
kubectl apply -f .\05-payment-service.yaml
kubectl apply -f .\06-facility-service.yaml
kubectl get pods
kubectl get services
