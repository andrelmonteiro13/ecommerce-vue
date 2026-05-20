import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerOrderService from '@/entities/customer-order/customer-order.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { PaymentMethod } from '@/shared/model/enumerations/payment-method.model';
import { PaymentStatus } from '@/shared/model/enumerations/payment-status.model';
import { type IPayment, Payment } from '@/shared/model/payment.model';

import PaymentService from './payment.service';

export default defineComponent({
  name: 'PaymentUpdate',
  setup() {
    const paymentService = inject('paymentService', () => new PaymentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const payment: Ref<IPayment> = ref(new Payment());

    const customerOrderService = inject('customerOrderService', () => new CustomerOrderService());

    const customerOrders: Ref<ICustomerOrder[]> = ref([]);
    const paymentMethodValues: Ref<string[]> = ref(Object.keys(PaymentMethod));
    const paymentStatusValues: Ref<string[]> = ref(Object.keys(PaymentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePayment = async paymentId => {
      try {
        const res = await paymentService().find(paymentId);
        res.paidDate = new Date(res.paidDate);
        payment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.paymentId) {
      retrievePayment(route.params.paymentId);
    }

    const initRelationships = () => {
      customerOrderService()
        .retrieve()
        .then(res => {
          customerOrders.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      method: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      amount: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      transactionCode: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      paidDate: {},
      order: {},
    };
    const v$ = useVuelidate(validationRules, payment as any);
    v$.value.$validate();

    return {
      paymentService,
      alertService,
      payment,
      previousState,
      paymentMethodValues,
      paymentStatusValues,
      isSaving,
      currentLanguage,
      customerOrders,
      v$,
      ...useDateFormat({ entityRef: payment }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.payment.id) {
        this.paymentService()
          .update(this.payment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.payment.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.paymentService()
          .create(this.payment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.payment.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
