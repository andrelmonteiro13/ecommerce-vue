import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import CustomerOrderItemDetails from './customer-order-item-details.vue';
import CustomerOrderItemService from './customer-order-item.service';

type CustomerOrderItemDetailsComponentType = InstanceType<typeof CustomerOrderItemDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const customerOrderItemSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('CustomerOrderItem Management Detail Component', () => {
    let customerOrderItemServiceStub: SinonStubbedInstance<CustomerOrderItemService>;
    let mountOptions: MountingOptions<CustomerOrderItemDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      customerOrderItemServiceStub = sinon.createStubInstance<CustomerOrderItemService>(CustomerOrderItemService);

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
          customerOrderItemService: () => customerOrderItemServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        customerOrderItemServiceStub.find.resolves(customerOrderItemSample);
        route = {
          params: {
            customerOrderItemId: `${123}`,
          },
        };
        const wrapper = shallowMount(CustomerOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.customerOrderItem).toMatchObject(customerOrderItemSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        customerOrderItemServiceStub.find.resolves(customerOrderItemSample);
        const wrapper = shallowMount(CustomerOrderItemDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
