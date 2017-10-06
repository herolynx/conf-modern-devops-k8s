# conf-modern-devops-k8s

Sample service that can be deployed in **Kubernetes** created for **demo purposes**.

Related presentation can be found [here](https://docs.google.com/presentation/d/1pOX8E1BLIDple6zvYGMR5v9Nceax4YD_sE_Jhdp8jTY/edit?usp=sharing)!

Project structure:

* `src`: code of microservice with docker build file
* `devops`: files related with Kubernetes
* `others`: other samples for demo purposes

**Demo agenda:**

* deployment 
* publishing via service
* rollback & rolling update
* config/secrets definition & reloading
* web-socket support (with TCP connections load balancing)

## API

* **GET** `/hello`: printing sample message using config
* **GET** `/secrets`: printing sample message using secrets
* **GET** `/probe/health`: doing health-check
* **POST** `/probe/health`: changing status of health-check
* **GET** `/probe/ready`: doing readiness check
* **POST** `/probe/ready`: changing status of readiness

## Build

1) Build project

```
mvn clean build
```

or

```
mvn clean istall
```

2) Build and push docker image

```
mvn clean package docker:build -DpushImage -DpushImageTags -DdockerImageTags=<VERSION>
```

## Local development

Pre-requisites:

    * [minikube](https://github.com/kubernetes/minikube)
    * Maven
    * Java 8

1) Run locally

```
./run.sh
```

##### Run on local Kubernetes (using minikube)

1) Deploy all

```
kubectl create -f devops
```

2) Access local cluster

```
minikube dashboard
```

3) Checking address of local service

```
minikube service k8s-java-sample --url
```

## DevOps with Kubernetes

Service is prepared to be deployed in Kubernetes.

1) Deploy all

```
kubectl create -f devops
```

2) Access cluster

```
kubectl proxy
```

Then open dashboard in web-browser: http://localhost:8001/ui

3) Make new deployment (after changing image version in `devops/deployment.yaml`)

```
kubectl apply -f devops/deployment.yaml
```

4) Reload of config (after changing properties in `devops/config.yaml`)

```
kubectl apply -f devops/config.yaml
```

##### Basic operations

1) Get pods

```
kubectl get pods
```

2) Get services

```
kubectl get services
```

3) Check logs of single pod

```
kubectl logs -f <pod_name>
```

If you have many containers in pod, name of container must be specified:

```
kubectl logs -f <pod_name> -c <container_name>
```

4) Deleting stuff

```
kubectl delete service|deployment|pod <name>
```

5) Rollout (provided that `revisionHistoryLimit` > 0 in `devops/deployment.yaml`)

```
kubectl rollout undo deployment/<name>
```

6) Scaling 

```
kubectl scale --replicas=<number> deployment/<name>
```

## Kubernetes - set-up

### Google Cloud Platform

1) Create a cluster (this step can take a few minutes to complete).

```
gcloud container clusters create k8s-demo-cluster
```

or if you need more powerful machines:

```
gcloud container clusters create k8s-demo-cluster --machine-type n1-standard-2
```

2) Ensure kubectl has authentication credentials:

```
gcloud auth application-default login
```

3) Get context for kubectl

```
gcloud container clusters get-credentials k8s-demo-cluster 
```

4) Resize number of nodes in cluster

```
gcloud container clusters resize k8s-demo-cluster --size SIZE
```

### Azure

1) Create cluster

```
az acs create \ 
    -g k8s \
    -n k8s-demo-cluster \ 
    --orchestrator-type kubernetes \
    --generate-ssh-keys 
```

2) Get config for kubectl

```
az acs kubernetes get-credentials --resource-group=k8s --name=k8s-demo-cluster
```

### AWS

Check instructions at [KOPS](https://github.com/kubernetes/kops)

## Monitoring

### Kubernetes custom metrics 

* [Sample project with custom metrics](https://medium.com/@marko.luksa/kubernetes-autoscaling-based-on-custom-metrics-without-using-a-host-port-b783ed6241ac) - not working!!!

### DataDog

1) Set your API key in `monitoring/datadog/dd-agent.yaml`

2) Create daemon set

```
kubectl create -f monitoring/datadog
```

### InfluxDB (for auto-scaling)

Check following description:

* [Kapacitor](https://docs.influxdata.com/kapacitor/v1.1/nodes/k8s_autoscale_node/)

* [Sample Kapacitor project with auto-scaling](https://github.com/influxdata/k8s-kapacitor-autoscale)

## Kubernetes - other samples

##### Pod

1) Creating pod manually:

```
kubectl create -f others/SamplePod.yaml
```

2) Checking output:

a) Log in to nginx-container

```
kubectl exec -it sample-pod -c nginx-container -- /bin/bash
```

b) Install curl

```
apt-get update
apt-get install curl
apt-get install less
```

c) Check output of `debian-container`

```
curl localhost
less /usr/share/nginx/html/index.html
```

d) Check logs

```
kubectl logs sample-pod -c nginx-container
```

##### Deployment

```
kubectl run hello-minikube --image=gcr.io/google_containers/echoserver:1.4 --port=8080
kubectl expose deployment hello-minikube --type=NodePort
```

## Web-sockets

1) Establish web-socket connection

```
curl --include \
     --no-buffer \
     --header "Connection: Upgrade" \
     --header "Upgrade: websocket" \
     --header "Host: localhost:8080" \
     --header "Origin: http://localhost:8080" \
     --header "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" \
     --header "Sec-WebSocket-Version: 13" \
     http://localhost:8080/web-socket
```