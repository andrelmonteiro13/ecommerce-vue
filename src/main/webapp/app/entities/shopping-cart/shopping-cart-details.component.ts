import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat } from '@/shared/composables';
import { type IShoppingCart } from '@/shared/model/shopping-cart.model';

import ShoppingCartService from './shopping-cart.service';

export default defineComponent({
  name: 'ShoppingCartDetails',
  setup() {
    const dateFormat = useDateFormat();
    const shoppingCartService = inject('shoppingCartService', () => new ShoppingCartService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shoppingCart: Ref<IShoppingCart> = ref({});

    const retrieveShoppingCart = async shoppingCartId => {
      try {
        const res = await shoppingCartService().find(shoppingCartId);
        shoppingCart.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shoppingCartId) {
      retrieveShoppingCart(route.params.shoppingCartId);
    }

    return {
      ...dateFormat,
      alertService,
      shoppingCart,

      previousState,
      t$: useI18n().t,
    };
  },
});
