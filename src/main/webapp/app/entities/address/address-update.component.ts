import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerService from '@/entities/customer/customer.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { Address, type IAddress } from '@/shared/model/address.model';
import { type ICustomer } from '@/shared/model/customer.model';

import AddressService from './address.service';

export default defineComponent({
  name: 'AddressUpdate',
  setup() {
    const addressService = inject('addressService', () => new AddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const address: Ref<IAddress> = ref(new Address());

    const customerService = inject('customerService', () => new CustomerService());

    const customers: Ref<ICustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAddress = async addressId => {
      try {
        const res = await addressService().find(addressId);
        address.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.addressId) {
      retrieveAddress(route.params.addressId);
    }

    const initRelationships = () => {
      customerService()
        .retrieve()
        .then(res => {
          customers.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      street: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 150 }).toString(), 150),
      },
      number: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 20 }).toString(), 20),
      },
      complement: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      district: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      city: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      state: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 2 }).toString(), 2),
      },
      zipCode: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 20 }).toString(), 20),
      },
      country: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 80 }).toString(), 80),
      },
      customer: {},
    };
    const v$ = useVuelidate(validationRules, address as any);
    v$.value.$validate();

    return {
      addressService,
      alertService,
      address,
      previousState,
      isSaving,
      currentLanguage,
      customers,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.address.id) {
        this.addressService()
          .update(this.address)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.address.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.addressService()
          .create(this.address)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.address.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
