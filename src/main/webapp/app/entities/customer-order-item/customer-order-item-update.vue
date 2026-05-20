<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.customerOrderItem.home.createOrEditLabel" data-cy="CustomerOrderItemCreateUpdateHeading">
          {{ t$('ecommerceApp.customerOrderItem.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="customerOrderItem.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customerOrderItem.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.quantity') }}</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="customer-order-item-quantity"
              data-cy="quantity"
              :class="{ valid: !v$.quantity.$invalid, invalid: v$.quantity.$invalid }"
              v-model.number="v$.quantity.$model"
              required
            />
            <div v-if="v$.quantity.$anyDirty && v$.quantity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.unitPrice') }}</label>
            <input
              type="number"
              class="form-control"
              name="unitPrice"
              id="customer-order-item-unitPrice"
              data-cy="unitPrice"
              :class="{ valid: !v$.unitPrice.$invalid, invalid: v$.unitPrice.$invalid }"
              v-model.number="v$.unitPrice.$model"
              required
            />
            <div v-if="v$.unitPrice.$anyDirty && v$.unitPrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.unitPrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.totalPrice') }}</label>
            <input
              type="number"
              class="form-control"
              name="totalPrice"
              id="customer-order-item-totalPrice"
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
            <label class="form-control-label" for="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.order') }}</label>
            <select class="form-control" id="customer-order-item-order" data-cy="order" name="order" v-model="customerOrderItem.order">
              <option :value="null"></option>
              <option
                :value="
                  customerOrderItem.order && customerOrderOption.id === customerOrderItem.order.id
                    ? customerOrderItem.order
                    : customerOrderOption
                "
                v-for="customerOrderOption in customerOrders"
                :key="customerOrderOption.id"
              >
                {{ customerOrderOption.orderNumber }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer-order-item">{{ t$('ecommerceApp.customerOrderItem.product') }}</label>
            <select
              class="form-control"
              id="customer-order-item-product"
              data-cy="product"
              name="product"
              v-model="customerOrderItem.product"
            >
              <option :value="null"></option>
              <option
                :value="
                  customerOrderItem.product && productOption.id === customerOrderItem.product.id ? customerOrderItem.product : productOption
                "
                v-for="productOption in products"
                :key="productOption.id"
              >
                {{ productOption.name }}
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
<script lang="ts" src="./customer-order-item-update.component.ts"></script>
