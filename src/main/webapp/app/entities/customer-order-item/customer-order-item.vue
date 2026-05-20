<template>
  <div>
    <h2 id="page-heading" data-cy="CustomerOrderItemHeading">
      <span id="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.customerOrderItem.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'CustomerOrderItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-customer-order-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.customerOrderItem.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && customerOrderItems?.length === 0">
      <span>{{ t$('ecommerceApp.customerOrderItem.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="customerOrderItems?.length > 0">
      <table class="table table-striped" aria-describedby="customerOrderItems">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('quantity')">
              <span>{{ t$('ecommerceApp.customerOrderItem.quantity') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('unitPrice')">
              <span>{{ t$('ecommerceApp.customerOrderItem.unitPrice') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'unitPrice'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('totalPrice')">
              <span>{{ t$('ecommerceApp.customerOrderItem.totalPrice') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalPrice'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('order.orderNumber')">
              <span>{{ t$('ecommerceApp.customerOrderItem.order') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.orderNumber'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('product.name')">
              <span>{{ t$('ecommerceApp.customerOrderItem.product') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.name'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customerOrderItem in customerOrderItems" :key="customerOrderItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CustomerOrderItemView', params: { customerOrderItemId: customerOrderItem.id } }">{{
                customerOrderItem.id
              }}</router-link>
            </td>
            <td>{{ customerOrderItem.quantity }}</td>
            <td>{{ customerOrderItem.unitPrice }}</td>
            <td>{{ customerOrderItem.totalPrice }}</td>
            <td>
              <div v-if="customerOrderItem.order">
                <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: customerOrderItem.order.id } }">{{
                  customerOrderItem.order.orderNumber
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="customerOrderItem.product">
                <router-link :to="{ name: 'ProductView', params: { productId: customerOrderItem.product.id } }">{{
                  customerOrderItem.product.name
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CustomerOrderItemView', params: { customerOrderItemId: customerOrderItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'CustomerOrderItemEdit', params: { customerOrderItemId: customerOrderItem.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(customerOrderItem)"
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
        <span id="ecommerceApp.customerOrderItem.delete.question" data-cy="customerOrderItemDeleteDialogHeading">{{
          t$('entity.delete.title')
        }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-customerOrderItem-heading">{{ t$('ecommerceApp.customerOrderItem.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-customerOrderItem"
            data-cy="entityConfirmDeleteButton"
            @click="removeCustomerOrderItem"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="customerOrderItems?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer-order-item.component.ts"></script>
