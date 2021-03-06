# conf-modern-devops-k8s

Sample micro-service that can be deployed in **Kubernetes** created for **demo purposes**.

Related presentation can be found [here](https://docs.google.com/presentation/d/1pOX8E1BLIDple6zvYGMR5v9Nceax4YD_sE_Jhdp8jTY/edit?usp=sharing)!

Project structure:

* `src`: code of microservice with docker build file
* `devops`: files related with Kubernetes
* `others`: other samples for demo purposes

**Demo agenda:**

The purposes of this demo & presentation is to show how micro-services can be managed using Kubernetes, thus following scenarios are covered:

* deployment 
* publishing via service & ingress
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
* `/web-socket`: establish web-socket connection (use `test_web_socket.sh` script, see `web-sockets` section)

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

7) Get basic info about your services

```
watch -n 1 kubectl get configmap,secrets,deployments,services,ingress,nodes,pods
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

## Web-sockets & Ingress

Support for load balancing is cloud specific since LB are provided externally from the cloud while publishing services.

For web-socket load balancing you need LB L4 (transport layer) in order to balance TCP connections.

So in most cases you'll have to create external LB in front of Kubernetes cluster in order to balance TPC connections.

Related documentation:

* [Web service load balancing](https://blog.vivekpanyam.com/scaling-a-web-service-load-balancing/)

* [Load balancing in GCP](https://cloud.google.com/container-engine/docs/tutorials/http-balancer)

#### Local environment

1) Make sure that `ingress` is enabled on `minikube`

```
minikube addons enable ingress
```

#### Test web-socket connections

1) Check address of `ingress` service

```
kubectl describe ing
```

and then check access to address (with follow redirects and insecure option on), i.e.:

```
curl -L -k http://192.168.99.100/hello
```

2) Create web-socket

```
./test_web_socket.sh <URL>
```

or use `http://www.websocket.org/echo.html` if you want to check it from `web-browser`.

3) Terminate pods (keeping minimum 1 alive) in order to check whether your web-socket connection will be terminated

```
kubectl delete pod <name>
```

## Troubleshooting 

#### Access via `ingress` doesn't work

Check status of ingress:

```
kubectl descibe ingress <name>
```

sample output:

```
14:34 $ kubectl get ingress
NAME              HOSTS     ADDRESS          PORTS     AGE
Every 1.0s: kubectl describe ingress main                                                                                                                      

Name:                   main
Namespace:              default
Address:                35.190.61.93
Default backend:        default-http-backend:80 (10.4.2.3:8080)
Rules:
  Host  Path    Backends
  ----  ----    --------
  *
        /       k8s-java-sample:80 (<none>)
Annotations:
  forwarding-rule:      k8s-fw-default-main--8cd284ff28c67446
  rewrite-target:       /
  target-proxy:         k8s-tp-default-main--8cd284ff28c67446
  url-map:              k8s-um-default-main--8cd284ff28c67446
  backends:             {"k8s-be-32460--8cd284ff28c67446":"Unknown","k8s-be-32598--8cd284ff28c67446":"Unknown"}
Events:
  FirstSeen     LastSeen        Count   From                    SubObjectPath   Type            Reason  Message
  ---------     --------        -----   ----                    -------------   --------        ------  -------
  3m            3m              1       loadbalancer-controller                 Normal          ADD     default/main
  2m            2m              1       loadbalancer-controller                 Normal          CREATE  ip: 35.190.61.93
  2m            2m              3       loadbalancer-controller                 Normal          Service no user specified default backend, using system default

```

If you have services in `Unknown` or `Unhealthy` status that means that there is some issue with LB.

In such case you have to go to your external LB and add backend services to change status to healthy (i.e. for GCP go to UI console and then `Container engine` -> `Discovery and load balancing`).

You might also want to try create `ingress` using different variants, i.e.:

* `fanout`: ingress_fanout.yaml

* `single service`: ingress_single_service.yml

That helps in example in case of `minikube`.

More details [here](https://kubernetes.io/docs/concepts/services-networking/ingress/).