import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import PaymentDetails from './payment-details.vue';
import PaymentService from './payment.service';

type PaymentDetailsComponentType = InstanceType<typeof PaymentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const paymentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Payment Management Detail Component', () => {
    let paymentServiceStub: SinonStubbedInstance<PaymentService>;
    let mountOptions: MountingOptions<PaymentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      paymentServiceStub = sinon.createStubInstance<PaymentService>(PaymentService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        toast: {
          show: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          paymentService: () => paymentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        paymentServiceStub.find.resolves(paymentSample);
        route = {
          params: {
            paymentId: `${123}`,
          },
        };
        const wrapper = shallowMount(PaymentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.payment).toMatchObject(paymentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        paymentServiceStub.find.resolves(paymentSample);
        const wrapper = shallowMount(PaymentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
