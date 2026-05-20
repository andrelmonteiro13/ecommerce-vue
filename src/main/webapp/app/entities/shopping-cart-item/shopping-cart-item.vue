<template>
  <div>
    <h2 id="page-heading" data-cy="ShoppingCartItemHeading">
      <span id="shopping-cart-item">{{ t$('ecommerceApp.shoppingCartItem.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.shoppingCartItem.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'ShoppingCartItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shopping-cart-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.shoppingCartItem.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shoppingCartItems?.length === 0">
      <span>{{ t$('ecommerceApp.shoppingCartItem.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="shoppingCartItems?.length > 0">
      <table class="table table-striped" aria-describedby="shoppingCartItems">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('quantity')">
              <span>{{ t$('ecommerceApp.shoppingCartItem.quantity') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('unitPrice')">
              <span>{{ t$('ecommerceApp.shoppingCartItem.unitPrice') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'unitPrice'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('cart.id')">
              <span>{{ t$('ecommerceApp.shoppingCartItem.cart') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'cart.id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('product.name')">
              <span>{{ t$('ecommerceApp.shoppingCartItem.product') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.name'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shoppingCartItem in shoppingCartItems" :key="shoppingCartItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShoppingCartItemView', params: { shoppingCartItemId: shoppingCartItem.id } }">{{
                shoppingCartItem.id
              }}</router-link>
            </td>
            <td>{{ shoppingCartItem.quantity }}</td>
            <td>{{ shoppingCartItem.unitPrice }}</td>
            <td>
              <div v-if="shoppingCartItem.cart">
                <router-link :to="{ name: 'ShoppingCartView', params: { shoppingCartId: shoppingCartItem.cart.id } }">{{
                  shoppingCartItem.cart.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="shoppingCartItem.product">
                <router-link :to="{ name: 'ProductView', params: { productId: shoppingCartItem.product.id } }">{{
                  shoppingCartItem.product.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ShoppingCartItemView', params: { shoppingCartItemId: shoppingCartItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ShoppingCartItemEdit', params: { shoppingCartItemId: shoppingCartItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(shoppingCartItem)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">{{ t$('entity.action.delete') }}</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #title>
        <span id="ecommerceApp.shoppingCartItem.delete.question" data-cy="shoppingCartItemDeleteDialogHeading">{{
          t$('entity.delete.title')
        }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shoppingCartItem-heading">{{ t$('ecommerceApp.shoppingCartItem.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shoppingCartItem"
            data-cy="entityConfirmDeleteButton"
            @click="removeShoppingCartItem"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="shoppingCartItems?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shopping-cart-item.component.ts"></script>
