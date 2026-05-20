<template>
  <div>
    <h2 id="page-heading" data-cy="ShoppingCartHeading">
      <span id="shopping-cart">{{ t$('ecommerceApp.shoppingCart.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.shoppingCart.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'ShoppingCartCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shopping-cart"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.shoppingCart.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shoppingCarts?.length === 0">
      <span>{{ t$('ecommerceApp.shoppingCart.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="shoppingCarts?.length > 0">
      <table class="table table-striped" aria-describedby="shoppingCarts">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('createdDate')">
              <span>{{ t$('ecommerceApp.shoppingCart.createdDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('updatedDate')">
              <span>{{ t$('ecommerceApp.shoppingCart.updatedDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('customer.email')">
              <span>{{ t$('ecommerceApp.shoppingCart.customer') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'customer.email'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shoppingCart in shoppingCarts" :key="shoppingCart.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShoppingCartView', params: { shoppingCartId: shoppingCart.id } }">{{
                shoppingCart.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(shoppingCart.createdDate) || '' }}</td>
            <td>{{ formatDateShort(shoppingCart.updatedDate) || '' }}</td>
            <td>
              <div v-if="shoppingCart.customer">
                <router-link :to="{ name: 'CustomerView', params: { customerId: shoppingCart.customer.id } }">{{
                  shoppingCart.customer.email
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'ShoppingCartView', params: { shoppingCartId: shoppingCart.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ShoppingCartEdit', params: { shoppingCartId: shoppingCart.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(shoppingCart)"
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
        <span id="ecommerceApp.shoppingCart.delete.question" data-cy="shoppingCartDeleteDialogHeading">{{
          t$('entity.delete.title')
        }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shoppingCart-heading">{{ t$('ecommerceApp.shoppingCart.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shoppingCart"
            data-cy="entityConfirmDeleteButton"
            @click="removeShoppingCart"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="shoppingCarts?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shopping-cart.component.ts"></script>
