import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useAlertService } from '@/shared/alert/alert.service';
import { type IAddress } from '@/shared/model/address.model';

import AddressService from './address.service';

export default defineComponent({
  name: 'AddressDetails',
  setup() {
    const addressService = inject('addressService', () => new AddressService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const address: Ref<IAddress> = ref({});

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

    return {
      alertService,
      address,

      previousState,
      t$: useI18n().t,
    };
  },
});
