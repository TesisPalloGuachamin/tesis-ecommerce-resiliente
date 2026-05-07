import { API_BASE_URL } from "./config";
import type { AuthResponse, Cart, CheckoutRequest, Listing, Product } from "./types";

type Method = "GET" | "POST" | "PATCH" | "DELETE";

type RequestOptions = {
  method?: Method;
  token?: string;
  body?: unknown;
};

const REQUEST_TIMEOUT_MS = 12000;

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      method: options.method ?? "GET",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
      },
      body: options.body ? JSON.stringify(options.body) : undefined,
      signal: controller.signal,
    });

    const text = await response.text();
    const payload = text ? JSON.parse(text) : null;

    if (!response.ok) {
      const message =
        payload?.message ??
        payload?.error ??
        `Solicitud rechazada por API (${response.status})`;
      throw new Error(message);
    }

    return payload as T;
  } catch (error) {
    if (error instanceof Error && error.name === "AbortError") {
      throw new Error("La API no respondio a tiempo.");
    }

    if (error instanceof SyntaxError) {
      throw new Error("La API devolvio una respuesta no valida.");
    }

    throw error;
  } finally {
    clearTimeout(timeout);
  }
}

export const api = {
  login(email: string, password: string) {
    return request<AuthResponse>("/api/v1/auth/login", {
      method: "POST",
      body: { email, password },
    });
  },

  getProducts(token: string) {
    return request<Product[]>("/api/v1/products", { token });
  },

  getProduct(token: string, productId: string) {
    return request<Product>(`/api/v1/products/${productId}`, { token });
  },

  getCart(token: string) {
    return request<Cart>("/api/v1/cart", { token });
  },

  addCartItem(token: string, productId: string, quantity = 1) {
    return request<Cart>("/api/v1/cart/items", {
      method: "POST",
      token,
      body: { productId, quantity },
    });
  },

  updateCartItem(token: string, itemId: string, quantity: number) {
    return request<Cart>(`/api/v1/cart/items/${itemId}`, {
      method: "PATCH",
      token,
      body: { quantity },
    });
  },

  deleteCartItem(token: string, itemId: string) {
    return request<Cart>(`/api/v1/cart/items/${itemId}`, {
      method: "DELETE",
      token,
    });
  },

  checkout(token: string, cartId: string) {
    return request<CheckoutRequest>("/api/v1/checkout", {
      method: "POST",
      token,
      body: { cartId },
    });
  },

  getCheckout(token: string, requestId: string) {
    return request<CheckoutRequest>(`/api/v1/checkout/${requestId}`, { token });
  },

  createListing(
    token: string,
    data: { title: string; description?: string; price: number; quantity: number },
  ) {
    return request<Listing>("/api/v1/listings", {
      method: "POST",
      token,
      body: data,
    });
  },

  getListings(token: string) {
    return request<Listing[]>("/api/v1/listings", { token });
  },

  getListingById(token: string, listingId: string) {
    return request<Listing>(`/api/v1/listings/${listingId}`, { token });
  },
};
