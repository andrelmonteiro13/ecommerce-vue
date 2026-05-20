<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="address">
        <h2 class="jh-entity-heading" data-cy="addressDetailsHeading">
          <span>{{ t$('ecommerceApp.address.detail.title') }}Address</span> {{ address.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.address.street') }}</span>
          </dt>
          <dd>
            <span>{{ address.street }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.number') }}</span>
          </dt>
          <dd>
            <span>{{ address.number }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.complement') }}</span>
          </dt>
          <dd>
            <span>{{ address.complement }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.district') }}</span>
          </dt>
          <dd>
            <span>{{ address.district }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.city') }}</span>
          </dt>
          <dd>
            <span>{{ address.city }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.state') }}</span>
          </dt>
          <dd>
            <span>{{ address.state }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.zipCode') }}</span>
          </dt>
          <dd>
            <span>{{ address.zipCode }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.country') }}</span>
          </dt>
          <dd>
            <span>{{ address.country }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.address.customer') }}</span>
          </dt>
          <dd>
            <div v-if="address.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: address.customer.id } }">{{
                address.customer.email
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link v-if="address.id" :to="{ name: 'AddressEdit', params: { addressId: address.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./address-details.component.ts"></script>
