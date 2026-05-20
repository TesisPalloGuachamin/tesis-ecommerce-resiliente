declare const process: {
  env?: {
    EXPO_PUBLIC_API_BASE_URL?: string;
    EXPO_PUBLIC_BACKEND_ENV?: string;
  };
};

export const API_BASE_URL =
  process.env?.EXPO_PUBLIC_API_BASE_URL?.replace(/\/$/, "") ??
  "http://localhost:8080";

const configuredEnvironment = process.env?.EXPO_PUBLIC_BACKEND_ENV?.trim();

function inferEnvironmentLabel(baseUrl: string) {
  if (configuredEnvironment) {
    return configuredEnvironment;
  }

  if (baseUrl.includes("localhost") || baseUrl.includes("127.0.0.1")) {
    return "Local";
  }

  if (baseUrl.includes("amazonaws.com")) {
    return "AWS";
  }

  if (baseUrl.includes("azure") || baseUrl.includes("cloudapp.azure.com")) {
    return "Azure";
  }

  return "Custom";
}

export const BACKEND_ENVIRONMENT_LABEL = inferEnvironmentLabel(API_BASE_URL);

export const DEMO_EMAIL = "mobile-int-01@example.com";
export const DEMO_PASSWORD = "password123";
