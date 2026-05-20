import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type ICustomerOrderItem } from '@/shared/model/customer-order-item.model';

import CustomerOrderItemService from './customer-order-item.service';

export default defineComponent({
  name: 'CustomerOrderItemDetails',
  setup() {
    const customerOrderItemService = inject('customerOrderItemService', () => new CustomerOrderItemService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const customerOrderItem: Ref<ICustomerOrderItem> = ref({});

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

    return {
      alertService,
      customerOrderItem,

      previousState,
      t$: useI18n().t,
    };
  },
});
