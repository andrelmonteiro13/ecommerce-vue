<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.shoppingCart.home.createOrEditLabel" data-cy="ShoppingCartCreateUpdateHeading">
          {{ t$('ecommerceApp.shoppingCart.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="shoppingCart.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shoppingCart.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shopping-cart">{{ t$('ecommerceApp.shoppingCart.createdDate') }}</label>
            <div class="d-flex">
              <input
                id="shopping-cart-createdDate"
                data-cy="createdDate"
                type="datetime-local"
                class="form-control"
                name="createdDate"
                :class="{ valid: !v$.createdDate.$invalid, invalid: v$.createdDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.createdDate.$model)"
                @change="updateInstantField('createdDate', $event)"
              />
            </div>
            <div v-if="v$.createdDate.$anyDirty && v$.createdDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shopping-cart">{{ t$('ecommerceApp.shoppingCart.updatedDate') }}</label>
            <div class="d-flex">
              <input
                id="shopping-cart-updatedDate"
                data-cy="updatedDate"
                type="datetime-local"
                class="form-control"
                name="updatedDate"
                :class="{ valid: !v$.updatedDate.$invalid, invalid: v$.updatedDate.$invalid }"
                :value="convertDateTimeFromServer(v$.updatedDate.$model)"
                @change="updateInstantField('updatedDate', $event)"
              />
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shopping-cart">{{ t$('ecommerceApp.shoppingCart.customer') }}</label>
            <select class="form-control" id="shopping-cart-customer" data-cy="customer" name="customer" v-model="shoppingCart.customer">
              <option :value="null"></option>
              <option
                :value="shoppingCart.customer && customerOption.id === shoppingCart.customer.id ? shoppingCart.customer : customerOption"
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
<script lang="ts" src="./shopping-cart-update.component.ts"></script>
