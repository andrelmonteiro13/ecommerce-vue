import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { type RouteLocation } from 'vue-router';

import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import AlertService from '@/shared/alert/alert.service';

import ShoppingCartDetails from './shopping-cart-details.vue';
import ShoppingCartService from './shopping-cart.service';

type ShoppingCartDetailsComponentType = InstanceType<typeof ShoppingCartDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shoppingCartSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShoppingCart Management Detail Component', () => {
    let shoppingCartServiceStub: SinonStubbedInstance<ShoppingCartService>;
    let mountOptions: MountingOptions<ShoppingCartDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shoppingCartServiceStub = sinon.createStubInstance<ShoppingCartService>(ShoppingCartService);

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
          shoppingCartService: () => shoppingCartServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shoppingCartServiceStub.find.resolves(shoppingCartSample);
        route = {
          params: {
            shoppingCartId: `${123}`,
          },
        };
        const wrapper = shallowMount(ShoppingCartDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shoppingCart).toMatchObject(shoppingCartSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shoppingCartServiceStub.find.resolves(shoppingCartSample);
        const wrapper = shallowMount(ShoppingCartDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
