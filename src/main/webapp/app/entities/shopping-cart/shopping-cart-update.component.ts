import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerService from '@/entities/customer/customer.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ICustomer } from '@/shared/model/customer.model';
import { type IShoppingCart, ShoppingCart } from '@/shared/model/shopping-cart.model';

import ShoppingCartService from './shopping-cart.service';

export default defineComponent({
  name: 'ShoppingCartUpdate',
  setup() {
    const shoppingCartService = inject('shoppingCartService', () => new ShoppingCartService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shoppingCart: Ref<IShoppingCart> = ref(new ShoppingCart());

    const customerService = inject('customerService', () => new CustomerService());

    const customers: Ref<ICustomer[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShoppingCart = async shoppingCartId => {
      try {
        const res = await shoppingCartService().find(shoppingCartId);
        res.createdDate = new Date(res.createdDate);
        res.updatedDate = new Date(res.updatedDate);
        shoppingCart.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shoppingCartId) {
      retrieveShoppingCart(route.params.shoppingCartId);
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
      createdDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      updatedDate: {},
      customer: {},
    };
    const v$ = useVuelidate(validationRules, shoppingCart as any);
    v$.value.$validate();

    return {
      shoppingCartService,
      alertService,
      shoppingCart,
      previousState,
      isSaving,
      currentLanguage,
      customers,
      v$,
      ...useDateFormat({ entityRef: shoppingCart }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shoppingCart.id) {
        this.shoppingCartService()
          .update(this.shoppingCart)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.shoppingCart.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shoppingCartService()
          .create(this.shoppingCart)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.shoppingCart.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
