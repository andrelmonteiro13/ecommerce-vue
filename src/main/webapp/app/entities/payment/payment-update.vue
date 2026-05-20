<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.payment.home.createOrEditLabel" data-cy="PaymentCreateUpdateHeading">
          {{ t$('ecommerceApp.payment.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="payment.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="payment.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.method') }}</label>
            <select
              class="form-control"
              name="method"
              :class="{ valid: !v$.method.$invalid, invalid: v$.method.$invalid }"
              v-model="v$.method.$model"
              id="payment-method"
              data-cy="method"
              required
            >
              <option
                v-for="paymentMethod in paymentMethodValues"
                :key="paymentMethod"
                :value="paymentMethod"
                :label="t$('ecommerceApp.PaymentMethod.' + paymentMethod)"
              >
                {{ paymentMethod }}
              </option>
            </select>
            <div v-if="v$.method.$anyDirty && v$.method.$invalid">
              <small class="form-text text-danger" v-for="error of v$.method.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.status') }}</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="payment-status"
              data-cy="status"
              required
            >
              <option
                v-for="paymentStatus in paymentStatusValues"
                :key="paymentStatus"
                :value="paymentStatus"
                :label="t$('ecommerceApp.PaymentStatus.' + paymentStatus)"
              >
                {{ paymentStatus }}
              </option>
            </select>
            <div v-if="v$.status.$anyDirty && v$.status.$invalid">
              <small class="form-text text-danger" v-for="error of v$.status.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.amount') }}</label>
            <input
              type="number"
              class="form-control"
              name="amount"
              id="payment-amount"
              data-cy="amount"
              :class="{ valid: !v$.amount.$invalid, invalid: v$.amount.$invalid }"
              v-model.number="v$.amount.$model"
              required
            />
            <div v-if="v$.amount.$anyDirty && v$.amount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.amount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.transactionCode') }}</label>
            <input
              type="text"
              class="form-control"
              name="transactionCode"
              id="payment-transactionCode"
              data-cy="transactionCode"
              :class="{ valid: !v$.transactionCode.$invalid, invalid: v$.transactionCode.$invalid }"
              v-model="v$.transactionCode.$model"
            />
            <div v-if="v$.transactionCode.$anyDirty && v$.transactionCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.transactionCode.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.paidDate') }}</label>
            <div class="d-flex">
              <input
                id="payment-paidDate"
                data-cy="paidDate"
                type="datetime-local"
                class="form-control"
                name="paidDate"
                :class="{ valid: !v$.paidDate.$invalid, invalid: v$.paidDate.$invalid }"
                :value="convertDateTimeFromServer(v$.paidDate.$model)"
                @change="updateInstantField('paidDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="payment">{{ t$('ecommerceApp.payment.order') }}</label>
            <select class="form-control" id="payment-order" data-cy="order" name="order" v-model="payment.order">
              <option :value="null"></option>
              <option
                :value="payment.order && customerOrderOption.id === payment.order.id ? payment.order : customerOrderOption"
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
<script lang="ts" src="./payment-update.component.ts"></script>
