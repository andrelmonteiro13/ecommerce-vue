<template>
  <div>
    <h2 id="page-heading" data-cy="CustomerOrderHeading">
      <span id="customer-order">{{ t$('ecommerceApp.customerOrder.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.customerOrder.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'CustomerOrderCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-customer-order"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.customerOrder.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && customerOrders?.length === 0">
      <span>{{ t$('ecommerceApp.customerOrder.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="customerOrders?.length > 0">
      <table class="table table-striped" aria-describedby="customerOrders">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('orderNumber')">
              <span>{{ t$('ecommerceApp.customerOrder.orderNumber') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'orderNumber'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('orderDate')">
              <span>{{ t$('ecommerceApp.customerOrder.orderDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'orderDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('status')">
              <span>{{ t$('ecommerceApp.customerOrder.status') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('subtotal')">
              <span>{{ t$('ecommerceApp.customerOrder.subtotal') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subtotal'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('discount')">
              <span>{{ t$('ecommerceApp.customerOrder.discount') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'discount'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('shippingCost')">
              <span>{{ t$('ecommerceApp.customerOrder.shippingCost') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'shippingCost'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('totalPrice')">
              <span>{{ t$('ecommerceApp.customerOrder.totalPrice') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalPrice'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('notes')">
              <span>{{ t$('ecommerceApp.customerOrder.notes') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'notes'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('customer.email')">
              <span>{{ t$('ecommerceApp.customerOrder.customer') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'customer.email'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('user.login')">
              <span>{{ t$('ecommerceApp.customerOrder.user') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('shippingAddress.id')">
              <span>{{ t$('ecommerceApp.customerOrder.shippingAddress') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'shippingAddress.id'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customerOrder in customerOrders" :key="customerOrder.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: customerOrder.id } }">{{
                customerOrder.id
              }}</router-link>
            </td>
            <td>{{ customerOrder.orderNumber }}</td>
            <td>{{ formatDateShort(customerOrder.orderDate) || '' }}</td>
            <td>{{ t$('ecommerceApp.CustomerOrderStatus.' + customerOrder.status) }}</td>
            <td>{{ customerOrder.subtotal }}</td>
            <td>{{ customerOrder.discount }}</td>
            <td>{{ customerOrder.shippingCost }}</td>
            <td>{{ customerOrder.totalPrice }}</td>
            <td>{{ customerOrder.notes }}</td>
            <td>
              <div v-if="customerOrder.customer">
                <router-link :to="{ name: 'CustomerView', params: { customerId: customerOrder.customer.id } }">{{
                  customerOrder.customer.email
                }}</router-link>
              </div>
            </td>
            <td>
              {{ customerOrder.user ? customerOrder.user.login : '' }}
            </td>
            <td>
              <div v-if="customerOrder.shippingAddress">
                <router-link :to="{ name: 'AddressView', params: { addressId: customerOrder.shippingAddress.id } }">{{
                  customerOrder.shippingAddress.id
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CustomerOrderView', params: { customerOrderId: customerOrder.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'CustomerOrderEdit', params: { customerOrderId: customerOrder.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(customerOrder)"
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
        <span id="ecommerceApp.customerOrder.delete.question" data-cy="customerOrderDeleteDialogHeading">{{
          t$('entity.delete.title')
        }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-customerOrder-heading">{{ t$('ecommerceApp.customerOrder.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-customerOrder"
            data-cy="entityConfirmDeleteButton"
            @click="removeCustomerOrder"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="customerOrders?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer-order.component.ts"></script>
