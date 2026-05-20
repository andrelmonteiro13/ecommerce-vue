import { Authority } from '@/shared/jhipster/constants';
const Entities = () => import('@/entities/entities.vue');

const Category = () => import('@/entities/category/category.vue');
const CategoryUpdate = () => import('@/entities/category/category-update.vue');
const CategoryDetails = () => import('@/entities/category/category-details.vue');

const Product = () => import('@/entities/product/product.vue');
const ProductUpdate = () => import('@/entities/product/product-update.vue');
const ProductDetails = () => import('@/entities/product/product-details.vue');

const Customer = () => import('@/entities/customer/customer.vue');
const CustomerUpdate = () => import('@/entities/customer/customer-update.vue');
const CustomerDetails = () => import('@/entities/customer/customer-details.vue');

const Address = () => import('@/entities/address/address.vue');
const AddressUpdate = () => import('@/entities/address/address-update.vue');
const AddressDetails = () => import('@/entities/address/address-details.vue');

const ShoppingCart = () => import('@/entities/shopping-cart/shopping-cart.vue');
const ShoppingCartUpdate = () => import('@/entities/shopping-cart/shopping-cart-update.vue');
const ShoppingCartDetails = () => import('@/entities/shopping-cart/shopping-cart-details.vue');

const ShoppingCartItem = () => import('@/entities/shopping-cart-item/shopping-cart-item.vue');
const ShoppingCartItemUpdate = () => import('@/entities/shopping-cart-item/shopping-cart-item-update.vue');
const ShoppingCartItemDetails = () => import('@/entities/shopping-cart-item/shopping-cart-item-details.vue');

const CustomerOrder = () => import('@/entities/customer-order/customer-order.vue');
const CustomerOrderUpdate = () => import('@/entities/customer-order/customer-order-update.vue');
const CustomerOrderDetails = () => import('@/entities/customer-order/customer-order-details.vue');

const CustomerOrderItem = () => import('@/entities/customer-order-item/customer-order-item.vue');
const CustomerOrderItemUpdate = () => import('@/entities/customer-order-item/customer-order-item-update.vue');
const CustomerOrderItemDetails = () => import('@/entities/customer-order-item/customer-order-item-details.vue');

const Payment = () => import('@/entities/payment/payment.vue');
const PaymentUpdate = () => import('@/entities/payment/payment-update.vue');
const PaymentDetails = () => import('@/entities/payment/payment-details.vue');

const Shipment = () => import('@/entities/shipment/shipment.vue');
const ShipmentUpdate = () => import('@/entities/shipment/shipment-update.vue');
const ShipmentDetails = () => import('@/entities/shipment/shipment-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'category',
      name: 'Category',
      component: Category,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/new',
      name: 'CategoryCreate',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/edit',
      name: 'CategoryEdit',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/view',
      name: 'CategoryView',
      component: CategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product',
      name: 'Product',
      component: Product,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/new',
      name: 'ProductCreate',
      component: ProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/:productId/edit',
      name: 'ProductEdit',
      component: ProductUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'product/:productId/view',
      name: 'ProductView',
      component: ProductDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer',
      name: 'Customer',
      component: Customer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/new',
      name: 'CustomerCreate',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/edit',
      name: 'CustomerEdit',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/view',
      name: 'CustomerView',
      component: CustomerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address',
      name: 'Address',
      component: Address,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/new',
      name: 'AddressCreate',
      component: AddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/:addressId/edit',
      name: 'AddressEdit',
      component: AddressUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'address/:addressId/view',
      name: 'AddressView',
      component: AddressDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart',
      name: 'ShoppingCart',
      component: ShoppingCart,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart/new',
      name: 'ShoppingCartCreate',
      component: ShoppingCartUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart/:shoppingCartId/edit',
      name: 'ShoppingCartEdit',
      component: ShoppingCartUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart/:shoppingCartId/view',
      name: 'ShoppingCartView',
      component: ShoppingCartDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart-item',
      name: 'ShoppingCartItem',
      component: ShoppingCartItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart-item/new',
      name: 'ShoppingCartItemCreate',
      component: ShoppingCartItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart-item/:shoppingCartItemId/edit',
      name: 'ShoppingCartItemEdit',
      component: ShoppingCartItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shopping-cart-item/:shoppingCartItemId/view',
      name: 'ShoppingCartItemView',
      component: ShoppingCartItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order',
      name: 'CustomerOrder',
      component: CustomerOrder,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order/new',
      name: 'CustomerOrderCreate',
      component: CustomerOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order/:customerOrderId/edit',
      name: 'CustomerOrderEdit',
      component: CustomerOrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order/:customerOrderId/view',
      name: 'CustomerOrderView',
      component: CustomerOrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order-item',
      name: 'CustomerOrderItem',
      component: CustomerOrderItem,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order-item/new',
      name: 'CustomerOrderItemCreate',
      component: CustomerOrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order-item/:customerOrderItemId/edit',
      name: 'CustomerOrderItemEdit',
      component: CustomerOrderItemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer-order-item/:customerOrderItemId/view',
      name: 'CustomerOrderItemView',
      component: CustomerOrderItemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'payment',
      name: 'Payment',
      component: Payment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'payment/new',
      name: 'PaymentCreate',
      component: PaymentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'payment/:paymentId/edit',
      name: 'PaymentEdit',
      component: PaymentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'payment/:paymentId/view',
      name: 'PaymentView',
      component: PaymentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment',
      name: 'Shipment',
      component: Shipment,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/new',
      name: 'ShipmentCreate',
      component: ShipmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/:shipmentId/edit',
      name: 'ShipmentEdit',
      component: ShipmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'shipment/:shipmentId/view',
      name: 'ShipmentView',
      component: ShipmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
