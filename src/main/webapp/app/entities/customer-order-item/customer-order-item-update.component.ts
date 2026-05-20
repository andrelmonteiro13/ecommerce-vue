import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerOrderService from '@/entities/customer-order/customer-order.service';
import ProductService from '@/entities/product/product.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { CustomerOrderItem, type ICustomerOrderItem } from '@/shared/model/customer-order-item.model';
import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { type IProduct } from '@/shared/model/product.model';

import CustomerOrderItemService from './customer-order-item.service';

export default defineComponent({
  name: 'CustomerOrderItemUpdate',
  setup() {
    const customerOrderItemService = inject('customerOrderItemService', () => new CustomerOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const customerOrderItem: Ref<ICustomerOrderItem> = ref(new CustomerOrderItem());

    const customerOrderService = inject('customerOrderService', () => new CustomerOrderService());

    const customerOrders: Ref<ICustomerOrder[]> = ref([]);

    const productService = inject('productService', () => new ProductService());

    const products: Ref<IProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCustomerOrderItem = async customerOrderItemId => {
      try {
        const res = await customerOrderItemService().find(customerOrderItemId);
        customerOrderItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.customerOrderItemId) {
      retrieveCustomerOrderItem(route.params.customerOrderItemId);
    }

    const initRelationships = () => {
      customerOrderService()
        .retrieve()
        .then(res => {
          customerOrders.value = res.data;
        });
      productService()
        .retrieve()
        .then(res => {
          products.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      quantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      unitPrice: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      totalPrice: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      order: {},
      product: {},
    };
    const v$ = useVuelidate(validationRules, customerOrderItem as any);
    v$.value.$validate();

    return {
      customerOrderItemService,
      alertService,
      customerOrderItem,
      previousState,
      isSaving,
      currentLanguage,
      customerOrders,
      products,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.customerOrderItem.id) {
        this.customerOrderItemService()
          .update(this.customerOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.customerOrderItem.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.customerOrderItemService()
          .create(this.customerOrderItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.customerOrderItem.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
