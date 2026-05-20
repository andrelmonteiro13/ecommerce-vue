import { defineComponent, provide } from 'vue';

import UserService from '@/entities/user/user.service';

import AddressService from './address/address.service';
import CategoryService from './category/category.service';
import CustomerService from './customer/customer.service';
import CustomerOrderService from './customer-order/customer-order.service';
import CustomerOrderItemService from './customer-order-item/customer-order-item.service';
import PaymentService from './payment/payment.service';
import ProductService from './product/product.service';
import ShipmentService from './shipment/shipment.service';
import ShoppingCartService from './shopping-cart/shopping-cart.service';
import ShoppingCartItemService from './shopping-cart-item/shopping-cart-item.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('categoryService', () => new CategoryService());
    provide('productService', () => new ProductService());
    provide('customerService', () => new CustomerService());
    provide('addressService', () => new AddressService());
    provide('shoppingCartService', () => new ShoppingCartService());
    provide('shoppingCartItemService', () => new ShoppingCartItemService());
    provide('customerOrderService', () => new CustomerOrderService());
    provide('customerOrderItemService', () => new CustomerOrderItemService());
    provide('paymentService', () => new PaymentService());
    provide('shipmentService', () => new ShipmentService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
