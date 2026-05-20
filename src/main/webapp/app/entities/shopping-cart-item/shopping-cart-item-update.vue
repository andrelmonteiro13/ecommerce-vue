<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.shoppingCartItem.home.createOrEditLabel" data-cy="ShoppingCartItemCreateUpdateHeading">
          {{ t$('ecommerceApp.shoppingCartItem.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="shoppingCartItem.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shoppingCartItem.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shopping-cart-item">{{ t$('ecommerceApp.shoppingCartItem.quantity') }}</label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="shopping-cart-item-quantity"
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
            <label class="form-control-label" for="shopping-cart-item">{{ t$('ecommerceApp.shoppingCartItem.unitPrice') }}</label>
            <input
              type="number"
              class="form-control"
              name="unitPrice"
              id="shopping-cart-item-unitPrice"
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
            <label class="form-control-label" for="shopping-cart-item">{{ t$('ecommerceApp.shoppingCartItem.cart') }}</label>
            <select class="form-control" id="shopping-cart-item-cart" data-cy="cart" name="cart" v-model="shoppingCartItem.cart">
              <option :value="null"></option>
              <option
                :value="
                  shoppingCartItem.cart && shoppingCartOption.id === shoppingCartItem.cart.id ? shoppingCartItem.cart : shoppingCartOption
                "
                v-for="shoppingCartOption in shoppingCarts"
                :key="shoppingCartOption.id"
              >
                {{ shoppingCartOption.id }}
              </option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="shopping-cart-item">{{ t$('ecommerceApp.shoppingCartItem.product') }}</label>
            <select
              class="form-control"
              id="shopping-cart-item-product"
              data-cy="product"
              name="product"
              v-model="shoppingCartItem.product"
            >
              <option :value="null"></option>
              <option
                :value="
                  shoppingCartItem.product && productOption.id === shoppingCartItem.product.id ? shoppingCartItem.product : productOption
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
<script lang="ts" src="./shopping-cart-item-update.component.ts"></script>
