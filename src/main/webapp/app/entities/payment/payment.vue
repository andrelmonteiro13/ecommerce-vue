<template>
  <div>
    <h2 id="page-heading" data-cy="PaymentHeading">
      <span id="payment">{{ t$('ecommerceApp.payment.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.payment.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'PaymentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-payment"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.payment.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && payments?.length === 0">
      <span>{{ t$('ecommerceApp.payment.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="payments?.length > 0">
      <table class="table table-striped" aria-describedby="payments">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('method')">
              <span>{{ t$('ecommerceApp.payment.method') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'method'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('status')">
              <span>{{ t$('ecommerceApp.payment.status') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('amount')">
              <span>{{ t$('ecommerceApp.payment.amount') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'amount'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('transactionCode')">
              <span>{{ t$('ecommerceApp.payment.transactionCode') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'transactionCode'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('paidDate')">
              <span>{{ t$('ecommerceApp.payment.paidDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'paidDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('order.orderNumber')">
              <span>{{ t$('ecommerceApp.payment.order') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.orderNumber'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="payment in payments" :key="payment.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'PaymentView', params: { paymentId: payment.id } }">{{ payment.id }}</router-link>
            </td>
            <td>{{ t$('ecommerceApp.PaymentMethod.' + payment.method) }}</td>
            <td>{{ t$('ecommerceApp.PaymentStatus.' + payment.status) }}</td>
            <td>{{ payment.amount }}</td>
            <td>{{ payment.transactionCode }}</td>
            <td>{{ formatDateShort(payment.paidDate) || '' }}</td>
            <td>
              <div v-if="payment.order">
                <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: payment.order.id } }">{{
                  payment.order.orderNumber
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'PaymentView', params: { paymentId: payment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'PaymentEdit', params: { paymentId: payment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(payment)"
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
        <span id="ecommerceApp.payment.delete.question" data-cy="paymentDeleteDialogHeading">{{ t$('entity.delete.title') }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-payment-heading">{{ t$('ecommerceApp.payment.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-payment"
            data-cy="entityConfirmDeleteButton"
            @click="removePayment"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="payments?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./payment.component.ts"></script>
