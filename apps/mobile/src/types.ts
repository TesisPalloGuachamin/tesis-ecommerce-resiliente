export type User = {
  id: string;
  email: string;
  name: string;
  enabled: boolean;
};

export type AuthResponse = {
  token: string;
  user: User;
};

export type Product = {
  id: string;
  sku: string;
  name: string;
  description: string;
  price: number | string;
  stock: number;
  active: boolean;
};

export type CartItem = {
  id: string;
  productId: string;
  product: Product;
  quantity: number;
  unitPrice: number | string;
  total: number | string;
};

export type Cart = {
  id: string;
  userId: string;
  items: CartItem[];
  total: number | string;
};

export type CheckoutRequest = {
  requestId: string;
  userId: string;
  totalAmount: number | string;
  status: "PENDING" | "COMPLETED" | "FAILED" | string;
};

export type Listing = {
  id: string;
  sellerId: string;
  title: string;
  description: string | null;
  price: number | string;
  quantity: number;
  status: string;
  createdAt: string;
  updatedAt: string;
};
