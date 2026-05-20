<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="shoppingCartItem">
        <h2 class="jh-entity-heading" data-cy="shoppingCartItemDetailsHeading">
          <span>{{ t$('ecommerceApp.shoppingCartItem.detail.title') }}ShoppingCartItem</span> {{ shoppingCartItem.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.shoppingCartItem.quantity') }}</span>
          </dt>
          <dd>
            <span>{{ shoppingCartItem.quantity }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shoppingCartItem.unitPrice') }}</span>
          </dt>
          <dd>
            <span>{{ shoppingCartItem.unitPrice }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shoppingCartItem.cart') }}</span>
          </dt>
          <dd>
            <div v-if="shoppingCartItem.cart">
              <router-link :to="{ name: 'ShoppingCartView', params: { shoppingCartId: shoppingCartItem.cart.id } }">{{
                shoppingCartItem.cart.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shoppingCartItem.product') }}</span>
          </dt>
          <dd>
            <div v-if="shoppingCartItem.product">
              <router-link :to="{ name: 'ProductView', params: { productId: shoppingCartItem.product.id } }">{{
                shoppingCartItem.product.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link
          v-if="shoppingCartItem.id"
          :to="{ name: 'ShoppingCartItemEdit', params: { shoppingCartItemId: shoppingCartItem.id } }"
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

<script lang="ts" src="./shopping-cart-item-details.component.ts"></script>
