<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="customerOrder">
        <h2 class="jh-entity-heading" data-cy="customerOrderDetailsHeading">
          <span>{{ t$('ecommerceApp.customerOrder.detail.title') }}CustomerOrder</span> {{ customerOrder.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.orderNumber') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.orderNumber }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.orderDate') }}</span>
          </dt>
          <dd>
            <span v-if="customerOrder.orderDate">{{ formatDateLong(customerOrder.orderDate) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.status') }}</span>
          </dt>
          <dd>
            <span>{{ t$('ecommerceApp.CustomerOrderStatus.' + customerOrder.status) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.subtotal') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.subtotal }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.discount') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.discount }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.shippingCost') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.shippingCost }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.totalPrice') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.totalPrice }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.notes') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrder.notes }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.customer') }}</span>
          </dt>
          <dd>
            <div v-if="customerOrder.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: customerOrder.customer.id } }">{{
                customerOrder.customer.email
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.user') }}</span>
          </dt>
          <dd>
            {{ customerOrder.user ? customerOrder.user.login : '' }}
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrder.shippingAddress') }}</span>
          </dt>
          <dd>
            <div v-if="customerOrder.shippingAddress">
              <router-link :to="{ name: 'AddressView', params: { addressId: customerOrder.shippingAddress.id } }">{{
                customerOrder.shippingAddress.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link
          v-if="customerOrder.id"
          :to="{ name: 'CustomerOrderEdit', params: { customerOrderId: customerOrder.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer-order-details.component.ts"></script>
