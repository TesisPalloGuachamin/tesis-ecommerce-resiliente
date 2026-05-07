import { StatusBar } from "expo-status-bar";
import { useMemo, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from "react-native";

import { api } from "./src/api";
import { API_BASE_URL, DEMO_EMAIL, DEMO_PASSWORD } from "./src/config";
import type { Cart, CartItem, CheckoutRequest, Product, User } from "./src/types";

type Screen = "login" | "catalog" | "detail" | "cart" | "confirmation";

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const formatMoney = (value?: number | string) => {
  const amount = Number(value ?? 0);
  return Number.isFinite(amount) ? `$${amount.toFixed(2)}` : `$${value}`;
};

export default function App() {
  const [screen, setScreen] = useState<Screen>("login");
  const [email, setEmail] = useState(DEMO_EMAIL);
  const [password, setPassword] = useState(DEMO_PASSWORD);
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [cart, setCart] = useState<Cart | null>(null);
  const [checkout, setCheckout] = useState<CheckoutRequest | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");

  const cartCount = useMemo(
    () => cart?.items.reduce((total, item) => total + item.quantity, 0) ?? 0,
    [cart],
  );

  const runWithError = async (task: () => Promise<void>) => {
    setBusy(true);
    setError("");
    try {
      await task();
    } catch (err) {
      setError(err instanceof Error ? err.message : "No se pudo completar la accion.");
    } finally {
      setBusy(false);
    }
  };

  const refreshCatalog = async (authToken = token) => {
    if (!authToken) {
      return;
    }

    const nextProducts = await api.getProducts(authToken);
    setProducts(nextProducts);
  };

  const refreshCart = async (authToken = token) => {
    if (!authToken) {
      return;
    }

    const nextCart = await api.getCart(authToken);
    setCart(nextCart);
  };

  const handleLogin = () =>
    runWithError(async () => {
      const auth = await api.login(email.trim(), password);
      setToken(auth.token);
      setUser(auth.user);
      const [nextProducts, nextCart] = await Promise.all([
        api.getProducts(auth.token),
        api.getCart(auth.token),
      ]);
      setProducts(nextProducts);
      setCart(nextCart);
      setCheckout(null);
      setScreen("catalog");
    });

  const handleLogout = () => {
    setToken(null);
    setUser(null);
    setProducts([]);
    setSelectedProduct(null);
    setCart(null);
    setCheckout(null);
    setError("");
    setScreen("login");
  };

  const openDetail = (product: Product) =>
    runWithError(async () => {
      setSelectedProduct(product);
      setScreen("detail");
      if (!token) {
        throw new Error("Sesion no disponible.");
      }

      const fullProduct = await api.getProduct(token, product.id);
      setSelectedProduct(fullProduct);
    });

  const addSelectedToCart = () =>
    runWithError(async () => {
      if (!token || !selectedProduct) {
        throw new Error("Producto o sesion no disponible.");
      }

      const nextCart = await api.addCartItem(token, selectedProduct.id, 1);
      setCart(nextCart);
      setScreen("cart");
    });

  const updateItemQuantity = (item: CartItem, quantity: number) =>
    runWithError(async () => {
      if (!token) {
        throw new Error("Sesion no disponible.");
      }

      const nextCart =
        quantity < 1
          ? await api.deleteCartItem(token, item.id)
          : await api.updateCartItem(token, item.id, quantity);
      setCart(nextCart);
    });

  const removeItem = (item: CartItem) =>
    runWithError(async () => {
      if (!token) {
        throw new Error("Sesion no disponible.");
      }

      const nextCart = await api.deleteCartItem(token, item.id);
      setCart(nextCart);
    });

  const submitCheckout = () =>
    runWithError(async () => {
      if (!token || !cart?.id) {
        throw new Error("Carrito no disponible.");
      }

      if (!cart.items.length) {
        throw new Error("El carrito esta vacio.");
      }

      const requested = await api.checkout(token, cart.id);
      setCheckout(requested);
      setScreen("confirmation");

      let latest = requested;
      for (let attempt = 0; attempt < 8 && latest.status === "PENDING"; attempt += 1) {
        await delay(1000);
        latest = await api.getCheckout(token, requested.requestId);
        setCheckout(latest);
      }
    });

  const title = screen === "login" ? "Tesis Mobile" : "Compra sandbox";

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="dark" />
      <View style={styles.shell}>
        <View style={styles.header}>
          <View>
            <Text style={styles.headerTitle}>{title}</Text>
            <Text style={styles.headerMeta}>{API_BASE_URL}</Text>
          </View>
          {token ? (
            <View style={styles.headerActions}>
              <Pressable style={styles.secondaryButton} onPress={() => setScreen("cart")}>
                <Text style={styles.secondaryButtonText}>Carrito {cartCount}</Text>
              </Pressable>
              <Pressable style={styles.ghostButton} onPress={handleLogout}>
                <Text style={styles.ghostButtonText}>Salir</Text>
              </Pressable>
            </View>
          ) : null}
        </View>

        {error ? (
          <View style={styles.errorBanner}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : null}

        {screen === "login" ? (
          <LoginScreen
            email={email}
            password={password}
            busy={busy}
            onEmail={setEmail}
            onPassword={setPassword}
            onLogin={handleLogin}
          />
        ) : null}

        {screen === "catalog" ? (
          <CatalogScreen
            products={products}
            busy={busy}
            user={user}
            onRefresh={() => runWithError(() => refreshCatalog())}
            onOpen={openDetail}
          />
        ) : null}

        {screen === "detail" && selectedProduct ? (
          <DetailScreen
            product={selectedProduct}
            busy={busy}
            onBack={() => setScreen("catalog")}
            onAdd={addSelectedToCart}
          />
        ) : null}

        {screen === "cart" ? (
          <CartScreen
            cart={cart}
            busy={busy}
            onBack={() => setScreen("catalog")}
            onRefresh={() => runWithError(() => refreshCart())}
            onUpdate={updateItemQuantity}
            onRemove={removeItem}
            onCheckout={submitCheckout}
          />
        ) : null}

        {screen === "confirmation" ? (
          <ConfirmationScreen
            checkout={checkout}
            busy={busy}
            onCatalog={() => setScreen("catalog")}
            onCart={() => setScreen("cart")}
          />
        ) : null}
      </View>
    </SafeAreaView>
  );
}

type LoginProps = {
  email: string;
  password: string;
  busy: boolean;
  onEmail: (value: string) => void;
  onPassword: (value: string) => void;
  onLogin: () => void;
};

function LoginScreen({ email, password, busy, onEmail, onPassword, onLogin }: LoginProps) {
  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.panel}>
        <Text style={styles.screenTitle}>Ingreso</Text>
        <Field label="Email" value={email} onChangeText={onEmail} keyboardType="email-address" />
        <Field label="Password" value={password} onChangeText={onPassword} secureTextEntry />
        <PrimaryButton label="Ingresar" busy={busy} onPress={onLogin} />
      </View>
    </ScrollView>
  );
}

type CatalogProps = {
  products: Product[];
  busy: boolean;
  user: User | null;
  onRefresh: () => void;
  onOpen: (product: Product) => void;
};

function CatalogScreen({ products, busy, user, onRefresh, onOpen }: CatalogProps) {
  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.sectionHeader}>
        <View>
          <Text style={styles.screenTitle}>Catalogo</Text>
          <Text style={styles.muted}>{user?.name ?? user?.email}</Text>
        </View>
        <Pressable style={styles.secondaryButton} onPress={onRefresh} disabled={busy}>
          <Text style={styles.secondaryButtonText}>Actualizar</Text>
        </Pressable>
      </View>

      {products.map((product) => (
        <Pressable key={product.id} style={styles.card} onPress={() => onOpen(product)}>
          <View style={styles.cardTopLine}>
            <Text style={styles.cardTitle}>{product.name}</Text>
            <Text style={styles.price}>{formatMoney(product.price)}</Text>
          </View>
          <Text style={styles.muted}>{product.description}</Text>
          <View style={styles.badgeRow}>
            <Text style={styles.badge}>{product.sku}</Text>
            <Text style={styles.badge}>Stock {product.stock}</Text>
          </View>
        </Pressable>
      ))}

      {!products.length && !busy ? <EmptyState text="No hay productos activos." /> : null}
      {busy ? <InlineLoader /> : null}
    </ScrollView>
  );
}

type DetailProps = {
  product: Product;
  busy: boolean;
  onBack: () => void;
  onAdd: () => void;
};

function DetailScreen({ product, busy, onBack, onAdd }: DetailProps) {
  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.sectionHeader}>
        <Text style={styles.screenTitle}>Detalle</Text>
        <Pressable style={styles.ghostButton} onPress={onBack}>
          <Text style={styles.ghostButtonText}>Volver</Text>
        </Pressable>
      </View>

      <View style={styles.panel}>
        <Text style={styles.detailTitle}>{product.name}</Text>
        <Text style={styles.detailPrice}>{formatMoney(product.price)}</Text>
        <Text style={styles.bodyText}>{product.description}</Text>
        <View style={styles.badgeRow}>
          <Text style={styles.badge}>{product.sku}</Text>
          <Text style={styles.badge}>Stock {product.stock}</Text>
          <Text style={styles.badge}>{product.active ? "Activo" : "Inactivo"}</Text>
        </View>
        <PrimaryButton
          label="Agregar al carrito"
          busy={busy}
          disabled={!product.active || product.stock < 1}
          onPress={onAdd}
        />
      </View>
    </ScrollView>
  );
}

type CartProps = {
  cart: Cart | null;
  busy: boolean;
  onBack: () => void;
  onRefresh: () => void;
  onUpdate: (item: CartItem, quantity: number) => void;
  onRemove: (item: CartItem) => void;
  onCheckout: () => void;
};

function CartScreen({
  cart,
  busy,
  onBack,
  onRefresh,
  onUpdate,
  onRemove,
  onCheckout,
}: CartProps) {
  const items = cart?.items ?? [];

  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.sectionHeader}>
        <Text style={styles.screenTitle}>Carrito</Text>
        <View style={styles.rowActions}>
          <Pressable style={styles.ghostButton} onPress={onBack}>
            <Text style={styles.ghostButtonText}>Catalogo</Text>
          </Pressable>
          <Pressable style={styles.secondaryButton} onPress={onRefresh} disabled={busy}>
            <Text style={styles.secondaryButtonText}>Actualizar</Text>
          </Pressable>
        </View>
      </View>

      {items.map((item) => (
        <View key={item.id} style={styles.card}>
          <View style={styles.cardTopLine}>
            <Text style={styles.cardTitle}>{item.product.name}</Text>
            <Text style={styles.price}>{formatMoney(item.total)}</Text>
          </View>
          <Text style={styles.muted}>Unidad {formatMoney(item.unitPrice)}</Text>
          <View style={styles.cartControls}>
            <Pressable
              style={styles.stepButton}
              onPress={() => onUpdate(item, item.quantity - 1)}
              disabled={busy}
            >
              <Text style={styles.stepText}>-</Text>
            </Pressable>
            <Text style={styles.quantity}>{item.quantity}</Text>
            <Pressable
              style={styles.stepButton}
              onPress={() => onUpdate(item, item.quantity + 1)}
              disabled={busy}
            >
              <Text style={styles.stepText}>+</Text>
            </Pressable>
            <Pressable style={styles.removeButton} onPress={() => onRemove(item)} disabled={busy}>
              <Text style={styles.removeButtonText}>Quitar</Text>
            </Pressable>
          </View>
        </View>
      ))}

      {!items.length ? <EmptyState text="Carrito vacio." /> : null}

      <View style={styles.totalBar}>
        <Text style={styles.totalLabel}>Total</Text>
        <Text style={styles.totalValue}>{formatMoney(cart?.total)}</Text>
      </View>
      <PrimaryButton
        label="Comprar"
        busy={busy}
        disabled={!items.length}
        onPress={onCheckout}
      />
    </ScrollView>
  );
}

type ConfirmationProps = {
  checkout: CheckoutRequest | null;
  busy: boolean;
  onCatalog: () => void;
  onCart: () => void;
};

function ConfirmationScreen({ checkout, busy, onCatalog, onCart }: ConfirmationProps) {
  const status = checkout?.status ?? "PENDING";
  const completed = status === "COMPLETED";
  const failed = status === "FAILED";

  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.panel}>
        <Text style={styles.screenTitle}>Confirmacion</Text>
        <Text style={[styles.statusText, failed ? styles.failedText : styles.completedText]}>
          {status}
        </Text>
        <Text style={styles.bodyText}>Solicitud: {checkout?.requestId ?? "pendiente"}</Text>
        <Text style={styles.bodyText}>Total: {formatMoney(checkout?.totalAmount)}</Text>
        {busy && !completed && !failed ? <InlineLoader /> : null}
        <View style={styles.confirmActions}>
          <Pressable style={styles.secondaryButton} onPress={onCatalog}>
            <Text style={styles.secondaryButtonText}>Catalogo</Text>
          </Pressable>
          <Pressable style={styles.ghostButton} onPress={onCart}>
            <Text style={styles.ghostButtonText}>Carrito</Text>
          </Pressable>
        </View>
      </View>
    </ScrollView>
  );
}

type FieldProps = {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  keyboardType?: "default" | "email-address";
  secureTextEntry?: boolean;
};

function Field({ label, value, onChangeText, keyboardType, secureTextEntry }: FieldProps) {
  return (
    <View style={styles.field}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={styles.input}
        value={value}
        autoCapitalize="none"
        autoCorrect={false}
        keyboardType={keyboardType}
        secureTextEntry={secureTextEntry}
        onChangeText={onChangeText}
      />
    </View>
  );
}

type ButtonProps = {
  label: string;
  busy?: boolean;
  disabled?: boolean;
  onPress: () => void;
};

function PrimaryButton({ label, busy, disabled, onPress }: ButtonProps) {
  const inactive = Boolean(disabled || busy);

  return (
    <Pressable
      style={[styles.primaryButton, inactive ? styles.disabledButton : null]}
      onPress={onPress}
      disabled={inactive}
    >
      {busy ? <ActivityIndicator color="#FFFFFF" /> : <Text style={styles.primaryButtonText}>{label}</Text>}
    </Pressable>
  );
}

function InlineLoader() {
  return (
    <View style={styles.loaderLine}>
      <ActivityIndicator color="#1F7A5A" />
      <Text style={styles.muted}>Procesando...</Text>
    </View>
  );
}

function EmptyState({ text }: { text: string }) {
  return (
    <View style={styles.emptyBox}>
      <Text style={styles.muted}>{text}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: "#F6F7F9",
  },
  shell: {
    flex: 1,
  },
  header: {
    minHeight: 86,
    paddingHorizontal: 18,
    paddingTop: 14,
    paddingBottom: 12,
    backgroundColor: "#FFFFFF",
    borderBottomWidth: 1,
    borderBottomColor: "#DDE3EA",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 12,
  },
  headerTitle: {
    fontSize: 22,
    lineHeight: 27,
    fontWeight: "800",
    color: "#17212B",
  },
  headerMeta: {
    marginTop: 3,
    fontSize: 11,
    lineHeight: 15,
    color: "#64748B",
  },
  headerActions: {
    flexDirection: "row",
    gap: 8,
    alignItems: "center",
  },
  content: {
    padding: 18,
    paddingBottom: 32,
    gap: 14,
  },
  panel: {
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#DDE3EA",
    padding: 18,
    gap: 14,
  },
  sectionHeader: {
    minHeight: 46,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    gap: 12,
  },
  screenTitle: {
    fontSize: 24,
    lineHeight: 30,
    fontWeight: "800",
    color: "#17212B",
  },
  detailTitle: {
    fontSize: 28,
    lineHeight: 34,
    fontWeight: "800",
    color: "#17212B",
  },
  detailPrice: {
    fontSize: 25,
    lineHeight: 31,
    fontWeight: "800",
    color: "#1F7A5A",
  },
  bodyText: {
    fontSize: 16,
    lineHeight: 23,
    color: "#334155",
  },
  muted: {
    fontSize: 14,
    lineHeight: 20,
    color: "#64748B",
  },
  card: {
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#DDE3EA",
    padding: 16,
    gap: 10,
  },
  cardTopLine: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-start",
    gap: 12,
  },
  cardTitle: {
    flex: 1,
    fontSize: 17,
    lineHeight: 23,
    fontWeight: "700",
    color: "#17212B",
  },
  price: {
    fontSize: 16,
    lineHeight: 22,
    fontWeight: "800",
    color: "#1F7A5A",
  },
  badgeRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  badge: {
    backgroundColor: "#E8EEF6",
    color: "#31506F",
    paddingHorizontal: 10,
    paddingVertical: 5,
    borderRadius: 8,
    overflow: "hidden",
    fontSize: 12,
    lineHeight: 16,
    fontWeight: "700",
  },
  field: {
    gap: 7,
  },
  label: {
    fontSize: 13,
    lineHeight: 18,
    fontWeight: "700",
    color: "#334155",
  },
  input: {
    height: 48,
    borderWidth: 1,
    borderColor: "#CBD5E1",
    backgroundColor: "#FFFFFF",
    borderRadius: 8,
    paddingHorizontal: 12,
    color: "#17212B",
    fontSize: 16,
  },
  primaryButton: {
    minHeight: 50,
    borderRadius: 8,
    backgroundColor: "#1F7A5A",
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 16,
  },
  primaryButtonText: {
    color: "#FFFFFF",
    fontSize: 16,
    lineHeight: 21,
    fontWeight: "800",
  },
  disabledButton: {
    opacity: 0.55,
  },
  secondaryButton: {
    minHeight: 38,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#9AB8CE",
    backgroundColor: "#F4FAFF",
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 12,
  },
  secondaryButtonText: {
    color: "#31506F",
    fontSize: 13,
    lineHeight: 18,
    fontWeight: "800",
  },
  ghostButton: {
    minHeight: 38,
    borderRadius: 8,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 12,
    backgroundColor: "#EEF2F7",
  },
  ghostButtonText: {
    color: "#334155",
    fontSize: 13,
    lineHeight: 18,
    fontWeight: "800",
  },
  errorBanner: {
    marginHorizontal: 18,
    marginTop: 14,
    borderRadius: 8,
    backgroundColor: "#FDECEC",
    borderWidth: 1,
    borderColor: "#F5B5B5",
    padding: 12,
  },
  errorText: {
    color: "#9B1C1C",
    fontSize: 14,
    lineHeight: 20,
    fontWeight: "700",
  },
  loaderLine: {
    minHeight: 36,
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
  },
  cartControls: {
    flexDirection: "row",
    alignItems: "center",
    gap: 9,
    flexWrap: "wrap",
  },
  stepButton: {
    width: 38,
    height: 38,
    borderRadius: 8,
    backgroundColor: "#E8EEF6",
    alignItems: "center",
    justifyContent: "center",
  },
  stepText: {
    fontSize: 22,
    lineHeight: 26,
    fontWeight: "800",
    color: "#31506F",
  },
  quantity: {
    minWidth: 34,
    textAlign: "center",
    fontSize: 17,
    lineHeight: 23,
    fontWeight: "800",
    color: "#17212B",
  },
  removeButton: {
    minHeight: 38,
    borderRadius: 8,
    backgroundColor: "#FFF1E8",
    paddingHorizontal: 12,
    alignItems: "center",
    justifyContent: "center",
  },
  removeButtonText: {
    color: "#9A4816",
    fontSize: 13,
    lineHeight: 18,
    fontWeight: "800",
  },
  rowActions: {
    flexDirection: "row",
    gap: 8,
    alignItems: "center",
  },
  totalBar: {
    minHeight: 54,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderColor: "#DDE3EA",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 10,
  },
  totalLabel: {
    fontSize: 16,
    lineHeight: 22,
    fontWeight: "700",
    color: "#334155",
  },
  totalValue: {
    fontSize: 21,
    lineHeight: 27,
    fontWeight: "800",
    color: "#17212B",
  },
  statusText: {
    fontSize: 30,
    lineHeight: 37,
    fontWeight: "900",
  },
  completedText: {
    color: "#1F7A5A",
  },
  failedText: {
    color: "#B42318",
  },
  confirmActions: {
    flexDirection: "row",
    gap: 10,
    flexWrap: "wrap",
  },
  emptyBox: {
    minHeight: 82,
    borderRadius: 8,
    borderWidth: 1,
    borderStyle: "dashed",
    borderColor: "#B9C5D2",
    alignItems: "center",
    justifyContent: "center",
    padding: 16,
  },
});
