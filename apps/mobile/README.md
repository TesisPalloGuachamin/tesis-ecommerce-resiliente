# Tesis Mobile Sandbox

Expo/React Native client for the academic purchase and listing demo.

## Backend endpoint

The app consumes a single backend endpoint configured at startup:

```bash
EXPO_PUBLIC_API_BASE_URL=http://<HOST>:8080
EXPO_PUBLIC_BACKEND_ENV=AWS principal
```

There is no automatic cloud failover in the mobile client. To point the app to
another recovered environment, update `.env.local` before starting Expo.

Examples:

```bash
# AWS principal
EXPO_PUBLIC_API_BASE_URL=http://<AWS_PRINCIPAL_HOST>:8080
EXPO_PUBLIC_BACKEND_ENV=AWS principal

# AWS alternate region
EXPO_PUBLIC_API_BASE_URL=http://<AWS_ALTERNATE_HOST>:8080
EXPO_PUBLIC_BACKEND_ENV=AWS alterna

# Azure recovery VM
EXPO_PUBLIC_API_BASE_URL=http://<AZURE_HOST>:8080
EXPO_PUBLIC_BACKEND_ENV=Azure
```

## Expo Go

```bash
npm run expo -- start --lan
```
