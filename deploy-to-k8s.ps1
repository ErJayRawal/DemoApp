#!/usr/bin/env pwsh
# Script to deploy DemoApp to Kubernetes in Docker Desktop

Write-Host "🚀 Starting DemoApp Kubernetes deployment..." -ForegroundColor Cyan

# Check if Docker is running
try {
    docker info > $null
    Write-Host "✅ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
    exit 1
}

# Check if Kubernetes is enabled
try {
    kubectl version --client > $null
    Write-Host "✅ kubectl is installed" -ForegroundColor Green
} catch {
    Write-Host "❌ kubectl is not installed. Please install kubectl and try again." -ForegroundColor Red
    exit 1
}

try {
    kubectl get nodes > $null
    Write-Host "✅ Kubernetes is running" -ForegroundColor Green
} catch {
    Write-Host "❌ Kubernetes is not running. Please enable Kubernetes in Docker Desktop and try again." -ForegroundColor Red
    Write-Host "   Go to Docker Desktop > Settings > Kubernetes > Enable Kubernetes" -ForegroundColor Yellow
    exit 1
}

# Build the application Docker image
Write-Host "🔨 Building DemoApp Docker image..." -ForegroundColor Cyan
docker build -t demoapp:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to build Docker image" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Docker image built successfully" -ForegroundColor Green

# Apply Kubernetes configurations
Write-Host "🔄 Applying Kubernetes configurations..." -ForegroundColor Cyan

# Create namespace first
kubectl apply -f k8s/namespace.yaml
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to create namespace" -ForegroundColor Red
    exit 1
}

# Apply all other configurations
kubectl apply -k k8s/
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Failed to apply Kubernetes configurations" -ForegroundColor Red
    exit 1
}
Write-Host "✅ Kubernetes configurations applied successfully" -ForegroundColor Green

# Wait for MongoDB to be ready
Write-Host "⏳ Waiting for MongoDB to be ready..." -ForegroundColor Cyan
kubectl wait --namespace demoapp --for=condition=ready pod -l app=mongodb --timeout=120s
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ MongoDB failed to start within the timeout period" -ForegroundColor Red
    exit 1
}
Write-Host "✅ MongoDB is ready" -ForegroundColor Green

# Wait for DemoApp to be ready
Write-Host "⏳ Waiting for DemoApp to be ready..." -ForegroundColor Cyan
kubectl wait --namespace demoapp --for=condition=ready pod -l app=demoapp --timeout=120s
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ DemoApp failed to start within the timeout period" -ForegroundColor Red
    exit 1
}
Write-Host "✅ DemoApp is ready" -ForegroundColor Green

# Add demoapp.local to hosts file
Write-Host "🔄 Adding demoapp.local to hosts file..." -ForegroundColor Cyan
$hostsPath = "$env:windir\System32\drivers\etc\hosts"
$hostsContent = Get-Content -Path $hostsPath
if (-not ($hostsContent -match "demoapp.local")) {
    Add-Content -Path $hostsPath -Value "127.0.0.1 demoapp.local" -Force
    Write-Host "✅ Added demoapp.local to hosts file" -ForegroundColor Green
} else {
    Write-Host "✅ demoapp.local already exists in hosts file" -ForegroundColor Green
}

# Get the NodePort for the ingress controller
Write-Host "🔍 Getting application access information..." -ForegroundColor Cyan

# Check if ingress-nginx is installed
$ingressInstalled = kubectl get pods -n ingress-nginx 2>$null
if (-not $ingressInstalled) {
    Write-Host "⚠️ Ingress controller not detected. Installing ingress-nginx..." -ForegroundColor Yellow
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.2/deploy/static/provider/cloud/deploy.yaml
    
    Write-Host "⏳ Waiting for ingress-nginx to be ready..." -ForegroundColor Cyan
    kubectl wait --namespace ingress-nginx --for=condition=ready pod -l app.kubernetes.io/component=controller --timeout=180s
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Ingress controller failed to start within the timeout period" -ForegroundColor Red
        Write-Host "⚠️ You can still access the application using port-forward:" -ForegroundColor Yellow
        Write-Host "   kubectl port-forward -n demoapp svc/demoapp 8080:8080" -ForegroundColor Yellow
    } else {
        Write-Host "✅ Ingress controller is ready" -ForegroundColor Green
    }
}

# Display access information
Write-Host "`n🎉 Deployment Complete! 🎉" -ForegroundColor Green
Write-Host "`nYou can access your application at:"
Write-Host "- Web UI: http://demoapp.local" -ForegroundColor Cyan
Write-Host "- Or via port-forward: kubectl port-forward -n demoapp svc/demoapp 8080:8080" -ForegroundColor Cyan
Write-Host "  Then access: http://localhost:8080" -ForegroundColor Cyan
Write-Host "`nMongoDB is accessible within the cluster at: mongodb.demoapp.svc.cluster.local:27017" -ForegroundColor Cyan

# Show running pods
Write-Host "`n📊 Deployed Resources:" -ForegroundColor Magenta
kubectl get all -n demoapp

Write-Host "`n📝 To view logs:" -ForegroundColor Magenta
Write-Host "kubectl logs -n demoapp -l app=demoapp -f" -ForegroundColor Cyan

Write-Host "`n🧹 To clean up:" -ForegroundColor Magenta
Write-Host "kubectl delete namespace demoapp" -ForegroundColor Cyan
