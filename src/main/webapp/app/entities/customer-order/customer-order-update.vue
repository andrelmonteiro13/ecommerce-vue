<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.customerOrder.home.createOrEditLabel" data-cy="CustomerOrderCreateUpdateHeading">
          {{ t$('ecommerceApp.customerOrder.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="customerOrder.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customerOrder.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.orderNumber') }}</label>
            <input
              type="text"
              class="form-control"
              name="orderNumber"
              id="customer-order-orderNumber"
              data-cy="orderNumber"
              :class="{ valid: !v$.orderNumber.$invalid, invalid: v$.orderNumber.$invalid }"
              v-model="v$.orderNumber.$model"
              required
            />
            <div v-if="v$.orderNumber.$anyDirty && v$.orderNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.orderDate') }}</label>
            <div class="d-flex">
              <input
                id="customer-order-orderDate"
                data-cy="orderDate"
                type="datetime-local"
                class="form-control"
                name="orderDate"
                :class="{ valid: !v$.orderDate.$invalid, invalid: v$.orderDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.orderDate.$model)"
                @change="updateInstantField('orderDate', $event)"
              />
            </div>
            <div v-if="v$.orderDate.$anyDirty && v$.orderDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.status') }}</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="customer-order-status"
              data-cy="status"
              required
            >
              <option
                v-for="customerOrderStatus in customerOrderStatusValues"
                :key="customerOrderStatus"
                :value="customerOrderStatus"
                :label="t$('ecommerceApp.CustomerOrderStatus.' + customerOrderStatus)"
              >
                {{ customerOrderStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.subtotal') }}</label>
            <input
              type="number"
              class="form-control"
              name="subtotal"
              id="customer-order-subtotal"
              data-cy="subtotal"
              :class="{ valid: !v$.subtotal.$invalid, invalid: v$.subtotal.$invalid }"
              v-model.number="v$.subtotal.$model"
              required
            />
            <div v-if="v$.subtotal.$anyDirty && v$.subtotal.$invalid">
              <small class="form-text text-danger" v-for="error of v$.subtotal.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.discount') }}</label>
            <input
              type="number"
              class="form-control"
              name="discount"
              id="customer-order-discount"
              data-cy="discount"
              :class="{ valid: !v$.discount.$invalid, invalid: v$.discount.$invalid }"
              v-model.number="v$.discount.$model"
            />
            <div v-if="v$.discount.$anyDirty && v$.discount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.discount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.shippingCost') }}</label>
            <input
              type="number"
              class="form-control"
              name="shippingCost"
              id="customer-order-shippingCost"
              data-cy="shippingCost"
              :class="{ valid: !v$.shippingCost.$invalid, invalid: v$.shippingCost.$invalid }"
              v-model.number="v$.shippingCost.$model"
            />
            <div v-if="v$.shippingCost.$anyDirty && v$.shippingCost.$invalid">
              <small class="form-text text-danger" v-for="error of v$.shippingCost.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.totalPrice') }}</label>
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="customer-order-totalPrice"
              data-cy="totalPrice"
              :class="{ valid: !v$.totalPrice.$invalid, invalid: v$.totalPrice.$invalid }"
              v-model.number="v$.totalPrice.$model"
              required
            />
            <div v-if="v$.totalPrice.$anyDirty && v$.totalPrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalPrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.notes') }}</label>
            <input
              type="text"
              class="form-control"
              name="notes"
              id="customer-order-notes"
              data-cy="notes"
              :class="{ valid: !v$.notes.$invalid, invalid: v$.notes.$invalid }"
              v-model="v$.notes.$model"
            />
            <div v-if="v$.notes.$anyDirty && v$.notes.$invalid">
              <small class="form-text text-danger" v-for="error of v$.notes.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.customer') }}</label>
            <select class="form-control" id="customer-order-customer" data-cy="customer" name="customer" v-model="customerOrder.customer">
              <option :value="null"></option>
              <option
                :value="customerOrder.customer && customerOption.id === customerOrder.customer.id ? customerOrder.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.email }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.user') }}</label>
            <select class="form-control" id="customer-order-user" data-cy="user" name="user" v-model="customerOrder.user">
              <option :value="null"></option>
              <option
                :value="customerOrder.user && userOption.id === customerOrder.user.id ? customerOrder.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order">{{ t$('ecommerceApp.customerOrder.shippingAddress') }}</label>
            <select
              class="form-control"
              id="customer-order-shippingAddress"
              data-cy="shippingAddress"
              name="shippingAddress"
              v-model="customerOrder.shippingAddress"
            >
              <option :value="null"></option>
              <option
                :value="
                  customerOrder.shippingAddress && addressOption.id === customerOrder.shippingAddress.id
                    ? customerOrder.shippingAddress
                    : addressOption
                "
                v-for="addressOption in addresses"
                :key="addressOption.id"
              >
                {{ addressOption.id }}
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
<script lang="ts" src="./customer-order-update.component.ts"></script>
