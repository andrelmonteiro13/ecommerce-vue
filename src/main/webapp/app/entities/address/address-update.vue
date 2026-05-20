<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.address.home.createOrEditLabel" data-cy="AddressCreateUpdateHeading">
          {{ t$('ecommerceApp.address.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="address.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="address.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.street') }}</label>
            <input
              type="text"
              class="form-control"
              name="street"
              id="address-street"
              data-cy="street"
              :class="{ valid: !v$.street.$invalid, invalid: v$.street.$invalid }"
              v-model="v$.street.$model"
              required
            />
            <div v-if="v$.street.$anyDirty && v$.street.$invalid">
              <small class="form-text text-danger" v-for="error of v$.street.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.number') }}</label>
            <input
              type="text"
              class="form-control"
              name="number"
              id="address-number"
              data-cy="number"
              :class="{ valid: !v$.number.$invalid, invalid: v$.number.$invalid }"
              v-model="v$.number.$model"
            />
            <div v-if="v$.number.$anyDirty && v$.number.$invalid">
              <small class="form-text text-danger" v-for="error of v$.number.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.complement') }}</label>
            <input
              type="text"
              class="form-control"
              name="complement"
              id="address-complement"
              data-cy="complement"
              :class="{ valid: !v$.complement.$invalid, invalid: v$.complement.$invalid }"
              v-model="v$.complement.$model"
            />
            <div v-if="v$.complement.$anyDirty && v$.complement.$invalid">
              <small class="form-text text-danger" v-for="error of v$.complement.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.district') }}</label>
            <input
              type="text"
              class="form-control"
              name="district"
              id="address-district"
              data-cy="district"
              :class="{ valid: !v$.district.$invalid, invalid: v$.district.$invalid }"
              v-model="v$.district.$model"
            />
            <div v-if="v$.district.$anyDirty && v$.district.$invalid">
              <small class="form-text text-danger" v-for="error of v$.district.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.city') }}</label>
            <input
              type="text"
              class="form-control"
              name="city"
              id="address-city"
              data-cy="city"
              :class="{ valid: !v$.city.$invalid, invalid: v$.city.$invalid }"
              v-model="v$.city.$model"
              required
            />
            <div v-if="v$.city.$anyDirty && v$.city.$invalid">
              <small class="form-text text-danger" v-for="error of v$.city.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.state') }}</label>
            <input
              type="text"
              class="form-control"
              name="state"
              id="address-state"
              data-cy="state"
              :class="{ valid: !v$.state.$invalid, invalid: v$.state.$invalid }"
              v-model="v$.state.$model"
              required
            />
            <div v-if="v$.state.$anyDirty && v$.state.$invalid">
              <small class="form-text text-danger" v-for="error of v$.state.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.zipCode') }}</label>
            <input
              type="text"
              class="form-control"
              name="zipCode"
              id="address-zipCode"
              data-cy="zipCode"
              :class="{ valid: !v$.zipCode.$invalid, invalid: v$.zipCode.$invalid }"
              v-model="v$.zipCode.$model"
              required
            />
            <div v-if="v$.zipCode.$anyDirty && v$.zipCode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.zipCode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.country') }}</label>
            <input
              type="text"
              class="form-control"
              name="country"
              id="address-country"
              data-cy="country"
              :class="{ valid: !v$.country.$invalid, invalid: v$.country.$invalid }"
              v-model="v$.country.$model"
              required
            />
            <div v-if="v$.country.$anyDirty && v$.country.$invalid">
              <small class="form-text text-danger" v-for="error of v$.country.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="address">{{ t$('ecommerceApp.address.customer') }}</label>
            <select class="form-control" id="address-customer" data-cy="customer" name="customer" v-model="address.customer">
              <option :value="null"></option>
              <option
                :value="address.customer && customerOption.id === address.customer.id ? address.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.email }}
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
<script lang="ts" src="./address-update.component.ts"></script>
