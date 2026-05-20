import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { Customer, type ICustomer } from '@/shared/model/customer.model';

import CustomerService from './customer.service';

export default defineComponent({
  name: 'CustomerUpdate',
  setup() {
    const customerService = inject('customerService', () => new CustomerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const customer: Ref<ICustomer> = ref(new Customer());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCustomer = async customerId => {
      try {
        const res = await customerService().find(customerId);
        res.createdDate = new Date(res.createdDate);
        customer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.customerId) {
      retrieveCustomer(route.params.customerId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      firstName: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 80 }).toString(), 80),
      },
      lastName: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 80 }).toString(), 80),
      },
      email: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phone: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 30 }).toString(), 30),
      },
      document: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 20 }).toString(), 20),
      },
      createdDate: {},
    };
    const v$ = useVuelidate(validationRules, customer as any);
    v$.value.$validate();

    return {
      customerService,
      alertService,
      customer,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: customer }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.customer.id) {
        this.customerService()
          .update(this.customer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.customer.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.customerService()
          .create(this.customer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.customer.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
