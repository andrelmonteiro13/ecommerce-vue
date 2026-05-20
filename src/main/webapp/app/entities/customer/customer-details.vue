<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="customer">
        <h2 class="jh-entity-heading" data-cy="customerDetailsHeading">
          <span>{{ t$('ecommerceApp.customer.detail.title') }}Customer</span> {{ customer.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.customer.firstName') }}</span>
          </dt>
          <dd>
            <span>{{ customer.firstName }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customer.lastName') }}</span>
          </dt>
          <dd>
            <span>{{ customer.lastName }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customer.email') }}</span>
          </dt>
          <dd>
            <span>{{ customer.email }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customer.phone') }}</span>
          </dt>
          <dd>
            <span>{{ customer.phone }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customer.document') }}</span>
          </dt>
          <dd>
            <span>{{ customer.document }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.customer.createdDate') }}</span>
          </dt>
          <dd>
            <span v-if="customer.createdDate">{{ formatDateLong(customer.createdDate) }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link v-if="customer.id" :to="{ name: 'CustomerEdit', params: { customerId: customer.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer-details.component.ts"></script>
