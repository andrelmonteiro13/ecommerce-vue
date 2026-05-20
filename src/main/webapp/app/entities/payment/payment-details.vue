<template>
  <div class="d-flex justify-content-center">
    <div class="col-8">
      <div v-if="payment">
        <h2 class="jh-entity-heading" data-cy="paymentDetailsHeading">
          <span>{{ t$('ecommerceApp.payment.detail.title') }}Payment</span> {{ payment.id }}
        </h2>
        <dl class="row-md jh-entity-details">
          <dt>
            <span>{{ t$('ecommerceApp.payment.method') }}</span>
          </dt>
          <dd>
            <span>{{ t$('ecommerceApp.PaymentMethod.' + payment.method) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.payment.status') }}</span>
          </dt>
          <dd>
            <span>{{ t$('ecommerceApp.PaymentStatus.' + payment.status) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.payment.amount') }}</span>
          </dt>
          <dd>
            <span>{{ payment.amount }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.payment.transactionCode') }}</span>
          </dt>
          <dd>
            <span>{{ payment.transactionCode }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.payment.paidDate') }}</span>
          </dt>
          <dd>
            <span v-if="payment.paidDate">{{ formatDateLong(payment.paidDate) }}</span>
          </dd>
          <dt>
            <span>{{ t$('ecommerceApp.payment.order') }}</span>
          </dt>
          <dd>
            <div v-if="payment.order">
              <router-link :to="{ name: 'CustomerOrderView', params: { customerOrderId: payment.order.id } }">{{
                payment.order.orderNumber
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.back') }}</span>
        </button>
        <router-link v-if="payment.id" :to="{ name: 'PaymentEdit', params: { paymentId: payment.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>{{ t$('entity.action.edit') }}</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./payment-details.component.ts"></script>
