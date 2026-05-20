import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import ProductService from '@/entities/product/product.service';
import ShoppingCartService from '@/entities/shopping-cart/shopping-cart.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useValidation } from '@/shared/composables';
import { type IProduct } from '@/shared/model/product.model';
import { type IShoppingCartItem, ShoppingCartItem } from '@/shared/model/shopping-cart-item.model';
import { type IShoppingCart } from '@/shared/model/shopping-cart.model';

import ShoppingCartItemService from './shopping-cart-item.service';

export default defineComponent({
  name: 'ShoppingCartItemUpdate',
  setup() {
    const shoppingCartItemService = inject('shoppingCartItemService', () => new ShoppingCartItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shoppingCartItem: Ref<IShoppingCartItem> = ref(new ShoppingCartItem());

    const shoppingCartService = inject('shoppingCartService', () => new ShoppingCartService());

    const shoppingCarts: Ref<IShoppingCart[]> = ref([]);

    const productService = inject('productService', () => new ProductService());

    const products: Ref<IProduct[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShoppingCartItem = async shoppingCartItemId => {
      try {
        const res = await shoppingCartItemService().find(shoppingCartItemId);
        shoppingCartItem.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shoppingCartItemId) {
      retrieveShoppingCartItem(route.params.shoppingCartItemId);
    }

    const initRelationships = () => {
      shoppingCartService()
        .retrieve()
        .then(res => {
          shoppingCarts.value = res.data;
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
      cart: {},
      product: {},
    };
    const v$ = useVuelidate(validationRules, shoppingCartItem as any);
    v$.value.$validate();

    return {
      shoppingCartItemService,
      alertService,
      shoppingCartItem,
      previousState,
      isSaving,
      currentLanguage,
      shoppingCarts,
      products,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shoppingCartItem.id) {
        this.shoppingCartItemService()
          .update(this.shoppingCartItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.shoppingCartItem.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shoppingCartItemService()
          .create(this.shoppingCartItem)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.shoppingCartItem.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
