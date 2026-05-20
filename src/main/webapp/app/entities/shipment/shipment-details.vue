<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="shipment">
        <h2 class="jh-entity-heading" data-cy="shipmentDetailsHeading">
          <span>{{ t$('ecommerceApp.shipment.detail.title') }}Shipment</span> {{ shipment.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.shipment.trackingNumber') }}</span>
          </dt>
          <dd>
            <span>{{ shipment.trackingNumber }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shipment.carrier') }}</span>
          </dt>
          <dd>
            <span>{{ shipment.carrier }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shipment.status') }}</span>
          </dt>
          <dd>
            <span>{{ t$('ecommerceApp.ShipmentStatus.' + shipment.status) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shipment.shippedDate') }}</span>
          </dt>
          <dd>
            <span v-if="shipment.shippedDate">{{ formatDateLong(shipment.shippedDate) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shipment.deliveredDate') }}</span>
          </dt>
          <dd>
            <span v-if="shipment.deliveredDate">{{ formatDateLong(shipment.deliveredDate) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.shipment.order') }}</span>
          </dt>
          <dd>
            <div v-if="shipment.order">
              <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: shipment.order.id } }">{{
                shipment.order.orderNumber
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link v-if="shipment.id" :to="{ name: 'ShipmentEdit', params: { shipmentId: shipment.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./shipment-details.component.ts"></script>
