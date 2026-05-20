<template>
  <div>
    <h2 id="page-heading" data-cy="ShipmentHeading">
      <span id="shipment">{{ t$('ecommerceApp.shipment.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.shipment.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'ShipmentCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-shipment"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.shipment.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shipments?.length === 0">
      <span>{{ t$('ecommerceApp.shipment.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="shipments?.length > 0">
      <table class="table table-striped" aria-describedby="shipments">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('trackingNumber')">
              <span>{{ t$('ecommerceApp.shipment.trackingNumber') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'trackingNumber'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('carrier')">
              <span>{{ t$('ecommerceApp.shipment.carrier') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'carrier'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('status')">
              <span>{{ t$('ecommerceApp.shipment.status') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('shippedDate')">
              <span>{{ t$('ecommerceApp.shipment.shippedDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'shippedDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('deliveredDate')">
              <span>{{ t$('ecommerceApp.shipment.deliveredDate') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deliveredDate'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('order.orderNumber')">
              <span>{{ t$('ecommerceApp.shipment.order') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.orderNumber'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shipment in shipments" :key="shipment.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipment.id } }">{{ shipment.id }}</router-link>
            </td>
            <td>{{ shipment.trackingNumber }}</td>
            <td>{{ shipment.carrier }}</td>
            <td>{{ t$('ecommerceApp.ShipmentStatus.' + shipment.status) }}</td>
            <td>{{ formatDateShort(shipment.shippedDate) || '' }}</td>
            <td>{{ formatDateShort(shipment.deliveredDate) || '' }}</td>
            <td>
              <div v-if="shipment.order">
                <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: shipment.order.id } }">{{
                  shipment.order.orderNumber
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'ShipmentView', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ShipmentEdit', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(shipment)"
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
        <span id="ecommerceApp.shipment.delete.question" data-cy="shipmentDeleteDialogHeading">{{ t$('entity.delete.title') }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shipment-heading">{{ t$('ecommerceApp.shipment.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shipment"
            data-cy="entityConfirmDeleteButton"
            @click="removeShipment"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="shipments?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shipment.component.ts"></script>
