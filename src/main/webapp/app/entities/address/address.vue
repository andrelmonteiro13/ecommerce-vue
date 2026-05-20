<template>
  <div>
    <h2 id="page-heading" data-cy="AddressHeading">
      <span id="address">{{ t$('ecommerceApp.address.home.title') }}</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info me-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span>{{ t$('ecommerceApp.address.home.refreshListLabel') }}</span>
        </button>
        <router-link :to="{ name: 'AddressCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-address"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>{{ t$('ecommerceApp.address.home.createLabel') }}</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && addresses?.length === 0">
      <span>{{ t$('ecommerceApp.address.home.notFound') }}</span>
    </div>
    <div class="table-responsive" v-if="addresses?.length > 0">
      <table class="table table-striped" aria-describedby="addresses">
        <thead>
          <tr>
            <th scope="col" @click="changeOrder('id')">
              <span>{{ t$('global.field.id') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('street')">
              <span>{{ t$('ecommerceApp.address.street') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'street'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('number')">
              <span>{{ t$('ecommerceApp.address.number') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'number'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('complement')">
              <span>{{ t$('ecommerceApp.address.complement') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'complement'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('district')">
              <span>{{ t$('ecommerceApp.address.district') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'district'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('city')">
              <span>{{ t$('ecommerceApp.address.city') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'city'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('state')">
              <span>{{ t$('ecommerceApp.address.state') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'state'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('zipCode')">
              <span>{{ t$('ecommerceApp.address.zipCode') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'zipCode'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('country')">
              <span>{{ t$('ecommerceApp.address.country') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'country'"></jhi-sort-indicator>
            </th>
            <th scope="col" @click="changeOrder('customer.email')">
              <span>{{ t$('ecommerceApp.address.customer') }}</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'customer.email'"></jhi-sort-indicator>
            </th>
            <th scope="col"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="address in addresses" :key="address.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AddressView', params: { addressId: address.id } }">{{ address.id }}</router-link>
            </td>
            <td>{{ address.street }}</td>
            <td>{{ address.number }}</td>
            <td>{{ address.complement }}</td>
            <td>{{ address.district }}</td>
            <td>{{ address.city }}</td>
            <td>{{ address.state }}</td>
            <td>{{ address.zipCode }}</td>
            <td>{{ address.country }}</td>
            <td>
              <div v-if="address.customer">
                <router-link :to="{ name: 'CustomerView', params: { customerId: address.customer.id } }">{{
                  address.customer.email
                }}</router-link>
              </div>
            </td>
            <td class="text-end">
              <div class="btn-group">
                <router-link :to="{ name: 'AddressView', params: { addressId: address.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.view') }}</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'AddressEdit', params: { addressId: address.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">{{ t$('entity.action.edit') }}</span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(address)"
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
        <span id="ecommerceApp.address.delete.question" data-cy="addressDeleteDialogHeading">{{ t$('entity.delete.title') }}</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-address-heading">{{ t$('ecommerceApp.address.delete.question', { id: removeId }) }}</p>
      </div>
      <template #footer>
        <div>
          <button type="button" class="btn btn-secondary" @click="closeDialog()">{{ t$('entity.action.cancel') }}</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-address"
            data-cy="entityConfirmDeleteButton"
            @click="removeAddress"
          >
            {{ t$('entity.action.delete') }}
          </button>
        </div>
      </template>
    </b-modal>
    <div v-show="addresses?.length > 0">
      <div class="d-flex justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :items-per-page="itemsPerPage"></jhi-item-count>
      </div>
      <div class="d-flex justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./address.component.ts"></script>
