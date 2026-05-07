declare const process: {
  env?: {
    EXPO_PUBLIC_API_BASE_URL?: string;
  };
};

export const API_BASE_URL =
  process.env?.EXPO_PUBLIC_API_BASE_URL?.replace(/\/$/, "") ??
  "http://localhost:8080";

export const DEMO_EMAIL = "mobile-int-01@example.com";
export const DEMO_PASSWORD = "password123";
