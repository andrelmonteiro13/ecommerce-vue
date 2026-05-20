<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="customerOrderItem">
        <h2 class="jh-entity-heading" data-cy="customerOrderItemDetailsHeading">
          <span>{{ t$('ecommerceApp.customerOrderItem.detail.title') }}CustomerOrderItem</span> {{ customerOrderItem.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.customerOrderItem.quantity') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrderItem.quantity }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrderItem.unitPrice') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrderItem.unitPrice }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrderItem.totalPrice') }}</span>
          </dt>
          <dd>
            <span>{{ customerOrderItem.totalPrice }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrderItem.order') }}</span>
          </dt>
          <dd>
            <div v-if="customerOrderItem.order">
              <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: customerOrderItem.order.id } }">{{
                customerOrderItem.order.orderNumber
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customerOrderItem.product') }}</span>
          </dt>
          <dd>
            <div v-if="customerOrderItem.product">
              <router-link :to="{ name: 'ProductView', params: { productId: customerOrderItem.product.id } }">{{
                customerOrderItem.product.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link
          v-if="customerOrderItem.id"
          :to="{ name: 'CustomerOrderItemEdit', params: { customerOrderItemId: customerOrderItem.id } }"
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

<script lang="ts" src="./customer-order-item-details.component.ts"></script>
