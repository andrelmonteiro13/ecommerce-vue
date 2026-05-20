import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IShoppingCartItem } from '@/shared/model/shopping-cart-item.model';

import ShoppingCartItemService from './shopping-cart-item.service';

export default defineComponent({
  name: 'ShoppingCartItemDetails',
  setup() {
    const shoppingCartItemService = inject('shoppingCartItemService', () => new ShoppingCartItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const shoppingCartItem: Ref<IShoppingCartItem> = ref({});

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

    return {
      alertService,
      shoppingCartItem,

      previousState,
      t$: useI18n().t,
    };
  },
});
