import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import { useVuelidate } from '@vuelidate/core';

import CustomerOrderService from '@/entities/customer-order/customer-order.service';
import { useAlertService } from '@/shared/alert/alert.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { type ICustomerOrder } from '@/shared/model/customer-order.model';
import { ShipmentStatus } from '@/shared/model/enumerations/shipment-status.model';
import { type IShipment, Shipment } from '@/shared/model/shipment.model';

import ShipmentService from './shipment.service';

export default defineComponent({
  name: 'ShipmentUpdate',
  setup() {
    const shipmentService = inject('shipmentService', () => new ShipmentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shipment: Ref<IShipment> = ref(new Shipment());

    const customerOrderService = inject('customerOrderService', () => new CustomerOrderService());

    const customerOrders: Ref<ICustomerOrder[]> = ref([]);
    const shipmentStatusValues: Ref<string[]> = ref(Object.keys(ShipmentStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'pt-br'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShipment = async shipmentId => {
      try {
        const res = await shipmentService().find(shipmentId);
        res.shippedDate = new Date(res.shippedDate);
        res.deliveredDate = new Date(res.deliveredDate);
        shipment.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shipmentId) {
      retrieveShipment(route.params.shipmentId);
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
      trackingNumber: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      carrier: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 100 }).toString(), 100),
      },
      status: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      shippedDate: {},
      deliveredDate: {},
      order: {},
    };
    const v$ = useVuelidate(validationRules, shipment as any);
    v$.value.$validate();

    return {
      shipmentService,
      alertService,
      shipment,
      previousState,
      shipmentStatusValues,
      isSaving,
      currentLanguage,
      customerOrders,
      v$,
      ...useDateFormat({ entityRef: shipment }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shipment.id) {
        this.shipmentService()
          .update(this.shipment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ecommerceApp.shipment.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shipmentService()
          .create(this.shipment)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ecommerceApp.shipment.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
