<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2 id="ecommerceApp.customer.home.createOrEditLabel" data-cy="CustomerCreateUpdateHeading">
          {{ t$('ecommerceApp.customer.home.createOrEditLabel') }}
        </h2>
        <div>
          <div class="mb-3" v-if="customer.id">
            <label for="id">{{ t$('global.field.id') }}</label>
            <input type="text" class="form-control" id="id" name="id" v-model="customer.id" readonly />
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.firstName') }}</label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="customer-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
              required
            />
            <div v-if="v$.firstName.$anyDirty && v$.firstName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.lastName') }}</label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="customer-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
              required
            />
            <div v-if="v$.lastName.$anyDirty && v$.lastName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lastName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.email') }}</label>
            <input
              type="text"
              class="form-control"
              name="email"
              id="customer-email"
              data-cy="email"
              :class="{ valid: !v$.email.$invalid, invalid: v$.email.$invalid }"
              v-model="v$.email.$model"
              required
            />
            <div v-if="v$.email.$anyDirty && v$.email.$invalid">
              <small class="form-text text-danger" v-for="error of v$.email.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.phone') }}</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="customer-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
            <div v-if="v$.phone.$anyDirty && v$.phone.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phone.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.document') }}</label>
            <input
              type="text"
              class="form-control"
              name="document"
              id="customer-document"
              data-cy="document"
              :class="{ valid: !v$.document.$invalid, invalid: v$.document.$invalid }"
              v-model="v$.document.$model"
            />
            <div v-if="v$.document.$anyDirty && v$.document.$invalid">
              <small class="form-text text-danger" v-for="error of v$.document.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-control-label" for="customer">{{ t$('ecommerceApp.customer.createdDate') }}</label>
            <div class="d-flex">
              <input
                id="customer-createdDate"
                data-cy="createdDate"
                type="datetime-local"
                class="form-control"
                name="createdDate"
                :class="{ valid: !v$.createdDate.$invalid, invalid: v$.createdDate.$invalid }"
                :value="convertDateTimeFromServer(v$.createdDate.$model)"
                @change="updateInstantField('createdDate', $event)"
              />
            </div>
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
<script lang="ts" src="./customer-update.component.ts"></script>
