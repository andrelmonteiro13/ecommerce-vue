<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.shipment.home.createOrEditLabel" data-cy="ShipmentCreateUpdateHeading">
          {{ t$('ecommerceApp.shipment.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="shipment.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shipment.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.trackingNumber') }}</label>
            <input
              type="text"
              class="form-control"
              name="trackingNumber"
              id="shipment-trackingNumber"
              data-cy="trackingNumber"
              :class="{ valid: !v$.trackingNumber.$invalid, invalid: v$.trackingNumber.$invalid }"
              v-model="v$.trackingNumber.$model"
            />
            <div v-if="v$.trackingNumber.$anyDirty && v$.trackingNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.trackingNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.carrier') }}</label>
            <input
              type="text"
              class="form-control"
              name="carrier"
              id="shipment-carrier"
              data-cy="carrier"
              :class="{ valid: !v$.carrier.$invalid, invalid: v$.carrier.$invalid }"
              v-model="v$.carrier.$model"
            />
            <div v-if="v$.carrier.$anyDirty && v$.carrier.$invalid">
              <small class="form-text text-danger" v-for="error of v$.carrier.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.status') }}</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="shipment-status"
              data-cy="status"
              required
            >
              <option
                v-for="shipmentStatus in shipmentStatusValues"
                :key="shipmentStatus"
                :value="shipmentStatus"
                :label="t$('ecommerceApp.ShipmentStatus.' + shipmentStatus)"
              >
                {{ shipmentStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.shippedDate') }}</label>
            <div class="d-flex">
              <input
                id="shipment-shippedDate"
                data-cy="shippedDate"
                type="datetime-local"
                class="form-control"
                name="shippedDate"
                :class="{ valid: !v$.shippedDate.$invalid, invalid: v$.shippedDate.$invalid }"
                :value="convertDateTimeFromServer(v$.shippedDate.$model)"
                @change="updateInstantField('shippedDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.deliveredDate') }}</label>
            <div class="d-flex">
              <input
                id="shipment-deliveredDate"
                data-cy="deliveredDate"
                type="datetime-local"
                class="form-control"
                name="deliveredDate"
                :class="{ valid: !v$.deliveredDate.$invalid, invalid: v$.deliveredDate.$invalid }"
                :value="convertDateTimeFromServer(v$.deliveredDate.$model)"
                @change="updateInstantField('deliveredDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shipment">{{ t$('ecommerceApp.shipment.order') }}</label>
            <select class="form-control" id="shipment-order" data-cy="order" name="order" v-model="shipment.order">
              <option :value="null"></option>
              <option
                :value="shipment.order && customerOrderOption.id === shipment.order.id ? shipment.order : customerOrderOption"
                v-for="customerOrderOption in customerOrders"
                :key="customerOrderOption.id"
              >
                {{ customerOrderOption.orderNumber }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.cancel') }}</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.save') }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./shipment-update.component.ts"></script>
